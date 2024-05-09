package org.crolopez.workplacereservationbot.booking.infrastructure.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.crolopez.workplacereservationbot.booking.domain.entities.BookingEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;
import org.crolopez.workplacereservationbot.booking.infrastructure.configuration.BookingPlatformConfig;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.BookingPlatformApi;
import org.crolopez.workplacereservationbot.booking.infrastructure.repositories.client.dto.*;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public List<BookingEntity> getParkingBookings() {
        return getFilteredBookings(config.matchingTag().parking());
    }

    @SneakyThrows
    @Override
    public List<BookingEntity> getOfficeBookings() {
        return getFilteredBookings(config.matchingTag().office());
    }

    private List<BookingEntity> getFilteredBookings(String bookingType) throws ApiException {
        String authToken = getAuthToken();
        BookingResponseDto response = getBookings(authToken);

        Map<String, LocationDto> locationMap = response.getLocations().stream()
                .filter(location -> location.getShortQualifier().equals(bookingType))
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        List<BookingEntity> bookingEntities = response.getBookings().stream()
                .filter(booking -> locationMap.containsKey(booking.getLocationId()))
                .map(booking -> {
                    LocationDto location = locationMap.get(booking.getLocationId());
                    return BookingEntity.builder()
                            .date(Date.from(booking.getTimeFrom().toInstant()))
                            .id(booking.getId())
                            .place(location.getQualifiedName())
                            .build();
                })
                .collect(Collectors.toList());

        return bookingEntities;
    }

    private BookingResponseDto getBookings(String authToken) throws ApiException {
        String cookie = getCookie(authToken);
        List<String> includes = List.of("locations", "visit", "facilities", "extras", "bookingSettings", "layouts");

        return bookingPlatformApi.getBookings(X_MATRIX_SOURCE, X_TIME_ZONE, cookie, includes);
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

}
