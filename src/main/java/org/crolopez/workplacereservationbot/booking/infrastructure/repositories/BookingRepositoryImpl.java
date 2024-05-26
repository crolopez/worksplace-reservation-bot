package org.crolopez.workplacereservationbot.booking.infrastructure.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;
import org.crolopez.workplacereservationbot.booking.infrastructure.configuration.BookingPlatformConfig;
import org.crolopez.workplacereservationbot.booking.infrastructure.entities.LoginDetails;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.BookingPlatformApi;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.dto.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.dto.AvailabilityDiscreteDto.StatusEnum.AVAILABLE;

@Singleton
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

    private final String X_MATRIX_SOURCE = "WEB";
    private final String X_TIME_ZONE = "Europe/Madrid";

    private Cache<String, LoginDetails> loginDetailsCache = Caffeine.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build();

    @Inject
    private BookingPlatformConfig config;

    @Inject
    ExecutorService taskExecutor;

    private BookingPlatformApi bookingPlatformApi;

    @PostConstruct
    public void init() {
        bookingPlatformApi = new BookingPlatformApi(getApiClient());
    }

    @SneakyThrows
    @Override
    public List<LocationEntity> getParkingBookings() {
        return getFilteredBookings(config.matchingTag().parking());
    }

    @SneakyThrows
    @Override
    public List<LocationEntity> getOfficeBookings() {
        return getFilteredBookings(config.matchingTag().office());
    }

    @SneakyThrows
    @Override
    public List<LocationEntity> getParkingAvailability(String date) {
        return getAvailability(date, config.keys().parking());
    }

    @SneakyThrows
    @Override
    public List<LocationEntity> getOfficeAvailability(String date) {
        return getAvailability(date, config.keys().office());
    }

    @SneakyThrows
    @Override
    public String bookParking(String date) { // TODO: TOIMPROVE
        LocationEntity bookedLocation = null;
        List<LocationEntity> locations = getAvailability(date, config.keys().parking());

        for (BookingPlatformConfig.BookingPreference preference : config.bookingPreferences()) {
            bookedLocation = tryBookingLocations(date, locations, preference);
            if (bookedLocation != null) {
                break;
            }
        }

        if (bookedLocation != null) {
            String message = String.format("Booked place for %s: %s", date, bookedLocation.getPlace());
            return message;
        }
        return "Could not book anything";
    }

    public CompletableFuture<Boolean> bookLocation(String date, String id) { // TODO: APPLICATION LAYER LOGIC | THIS CLASS MUST BE REFACTORED?
        LoginDetails loginDetails = getLoginDetails();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .bookingGroup(BookingGroupDto.builder()
                        .repeatEndDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .build())
                .attendees(List.of())
                .extraRequests(List.of())
                .locationId(id)
                .owner(IdentityDto.builder()
                        .email(loginDetails.getEmail())
                        .id(loginDetails.getPersonId())
                        .name(loginDetails.getName())
                        .build())
                .ownerIsAttendee(true)
                .source("WEB")
                .timeFrom(date + "T08:00:00.000")
                .timeTo(date + "T19:00:00.000")
                .build();

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Trying to book {} for {}", id, date);
                bookingPlatformApi.book(X_MATRIX_SOURCE, X_TIME_ZONE,
                        getLoginDetails().getCookie(), bookingRequestDto);
                log.info("Successful booking for {}", id);
                return true;
            } catch (ApiException e) {
                log.warn("Could not book {} for {}. Error: {}", id, date, e.getMessage());
                return false;
            }
        }, taskExecutor);
    }

    private List<LocationEntity> getFilteredBookings(String bookingType) throws ApiException {
        UserBookingResponseDto response = getBookings();

        Map<String, LocationDto> locationMap = response.getLocations().stream()
                .filter(location -> location.getShortQualifier().equals(bookingType))
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        Map<String, AncestorLocationDto> ancestors = response.getAncestorLocations().stream()
                .collect(Collectors.toMap(AncestorLocationDto::getId, Function.identity()));

        List<LocationEntity> bookingEntities = response.getBookings().stream()
                .filter(booking -> locationMap.containsKey(booking.getLocationId()))
                .map(booking -> {
                    LocationDto location = locationMap.get(booking.getLocationId());
                    AncestorLocationDto ancestor = ancestors.get(location.getParentId());
                    return LocationEntity.builder()
                            .date(Date.from(booking.getTimeFrom().toInstant()))
                            .id(booking.getId())
                            .place(location.getQualifiedName())
                            .space(ancestor.getName())
                            .build();
                })
                .collect(Collectors.toList());

        return bookingEntities;
    }

    private UserBookingResponseDto getBookings() throws ApiException {
        List<String> includes = List.of("locations", "visit", "facilities", "extras", "bookingSettings", "layouts");

        return bookingPlatformApi.getUserBookings(X_MATRIX_SOURCE, X_TIME_ZONE, getLoginDetails().getCookie(),
                includes);
    }

    private String getCookie(String token) {
        return String.format("MatrixAuthToken=%s", token);
    }

    @SneakyThrows
    private LoginDetails getLoginDetails() {
        LoginDetails cachedLoginDetails = loginDetailsCache.getIfPresent("login");
        if (cachedLoginDetails != null) {
            log.info("Returning cached login details");
            return cachedLoginDetails;
        }

        log.info("Authenticating user");

        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .password(config.login().password())
                .username(config.login().user())
                .build();

        ApiResponse<LoginResponseDto> response = bookingPlatformApi.loginWithHttpInfo(X_MATRIX_SOURCE, X_TIME_ZONE,
                loginRequest);

        LoginResponseDto loginResponseDto = response.getData();
        LoginDetails newAuthDetails = LoginDetails.builder()
                .name(loginResponseDto.getName())
                .email(loginResponseDto.getEmail())
                .organisationId(loginResponseDto.getOrganisationId())
                .personId(loginResponseDto.getPersonId())
                .cookie(getCookie(getAuthTokenFromResponse(response)))
                .build();

        loginDetailsCache.put("login", newAuthDetails);
        return newAuthDetails;
    }

    private ApiClient getApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setHost(config.hostname());
        apiClient.setScheme("https");
        return apiClient;
    }

    private String getAuthTokenFromResponse(ApiResponse<LoginResponseDto> response) {
        final String cookieHeader = "set-cookie";
        final String authTokenKey = "MatrixAuthToken";
        Pattern pattern = Pattern.compile(authTokenKey + "=([^;]+)");

        List<String> cookieHeaders = response.getHeaders().get(cookieHeader);

        return cookieHeaders.stream()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .findFirst()
                .orElse(null);
    }

    private List<LocationEntity> getAvailability(String date, BookingPlatformConfig.KeyNode key) throws ApiException {
        List<String> includes = List.of("locations", "facilities", "layouts", "bookingSettings", "floorplan",
                "bookings", "discrete");
        List<String> status = List.of("available", "unavailable", "booked");

        String from = date + "T00:00";
        String to = date + "T23:59";

        ApiResponse<AvailabilityResponseDto> response = bookingPlatformApi.getAvailabilityWithHttpInfo(from, to,
                key.bc(), key.l(), includes, status, X_MATRIX_SOURCE, X_TIME_ZONE, getLoginDetails().getCookie());
        List<AvailabilityDiscreteDto> availableLocations = response.getData().getDiscreteAvailability();
        List<AvailabilityLocationDto> locations = response.getData().getLocations();

        Map<String, AvailabilityLocationDto> locationMap = locations.stream()
                .collect(Collectors.toMap(AvailabilityLocationDto::getId, Function.identity()));

        return availableLocations.stream()
                .filter(availableLocation -> availableLocation.getStatus().equals(AVAILABLE))
                .filter(availableLocation -> locationMap.containsKey(availableLocation.getLocationId()))
                .map(availableLocation -> {
                    AvailabilityLocationDto locationDto = locationMap.get(availableLocation.getLocationId());
                    return LocationEntity.builder()
                            .date(Date.valueOf(date))
                            .place(locationDto.getName())
                            .id(locationDto.getId())
                            .space(locationDto.getLongQualifier())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private LocationEntity tryBookingLocations(String date, List<LocationEntity> locations, BookingPlatformConfig.BookingPreference preference) {
        List<LocationEntity> filteredLocations = locations.stream()
                .filter(location -> location.getSpace().equals(preference.space()))
                .collect(Collectors.toList());

        for (String priority : preference.priority()) {
            List<LocationEntity> priorityLocations = filterLocationsByPriority(filteredLocations, priority);
            List<CompletableFuture<LocationEntity>> futures = priorityLocations.stream()
                    .map(location -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return bookLocation(date, location.getId())
                                    .get(config.bookingBehaviour().bookTimeout(), TimeUnit.SECONDS)
                                        ? location
                                        : null;
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            log.warn("Error or timeout booking location {} for {}: {}", location.getId(), date, e.getMessage());
                            return null;
                        }
                    }, taskExecutor))
                    .collect(Collectors.toList());

            if (futures.isEmpty()) {
                continue;
            }

            try {
                CompletableFuture<LocationEntity> firstCompleted = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(result -> (LocationEntity) result);

                LocationEntity bookedLocation = firstCompleted.get();
                if (bookedLocation != null) {
                    return bookedLocation;
                }
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                log.error("Error booking location", e);
            }
        }
        return null;
    }

    private List<LocationEntity> filterLocationsByPriority(List<LocationEntity> locations, String priority) {
        Pattern pattern = Pattern.compile(priority, Pattern.CASE_INSENSITIVE);
        return locations.stream()
                .filter(location -> pattern.matcher(location.getPlace()).find())
                .collect(Collectors.toList());
    }

}
