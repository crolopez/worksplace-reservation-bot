package org.crolopez.workplacereservationbot.booking.infrastructure.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;
import org.crolopez.workplacereservationbot.booking.infrastructure.configuration.BookingPlatformConfig;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.BookingPlatformApi;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.dto.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.dto.AvailabilityDiscreteDto.StatusEnum.AVAILABLE;

@Singleton
public class BookingRepositoryImpl implements BookingRepository {

    private final String X_MATRIX_SOURCE = "WEB";
    private final String X_TIME_ZONE = "Europe/Madrid";

    @Inject
    private BookingPlatformConfig config;

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

    private List<LocationEntity> getFilteredBookings(String bookingType) throws ApiException {
        String authToken = getAuthToken();
        UserBookingResponseDto response = getBookings(authToken);

        Map<String, LocationDto> locationMap = response.getLocations().stream()
                .filter(location -> location.getShortQualifier().equals(bookingType))
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        List<LocationEntity> bookingEntities = response.getBookings().stream()
                .filter(booking -> locationMap.containsKey(booking.getLocationId()))
                .map(booking -> {
                    LocationDto location = locationMap.get(booking.getLocationId());
                    return LocationEntity.builder()
                            .date(Date.from(booking.getTimeFrom().toInstant()))
                            .id(booking.getId())
                            .place(location.getQualifiedName())
                            .build();
                })
                .collect(Collectors.toList());

        return bookingEntities;
    }

    private UserBookingResponseDto getBookings(String authToken) throws ApiException {
        String cookie = getCookie(authToken);
        List<String> includes = List.of("locations", "visit", "facilities", "extras", "bookingSettings", "layouts");

        return bookingPlatformApi.getUserBookings(X_MATRIX_SOURCE, X_TIME_ZONE, cookie, includes);
    }

    private String getCookie(String authToken) {
        return String.format("MatrixAuthToken=%s", authToken);
    }

    private String getAuthToken() throws ApiException {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .password(config.login().password())
                .username(config.login().user())
                .build();

        ApiResponse<Login200Response> response = bookingPlatformApi.loginWithHttpInfo(X_MATRIX_SOURCE, X_TIME_ZONE,
                loginRequest);

        return getAuthTokenFromResponse(response);
    }

    private ApiClient getApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setHost(config.hostname());
        apiClient.setScheme("https");
        return apiClient;
    }

    private String getAuthTokenFromResponse(ApiResponse<Login200Response> response) {
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
        String authToken = getAuthToken();
        String cookie = getCookie(authToken);
        List<String> includes = List.of("locations", "facilities", "layouts", "bookingSettings", "floorplan", "bookings", "discrete");
        List<String> status = List.of("available", "unavailable", "booked");

        String from = date + "T00:00";
        String to = date + "T23:59";

        ApiResponse<AvailabilityResponseDto> response = bookingPlatformApi.getAvailabilityWithHttpInfo(from, to,
                key.bc(), key.l(), includes, status, X_MATRIX_SOURCE, X_TIME_ZONE, cookie);
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
                            .build();
                })
                .collect(Collectors.toList());
    }

}
