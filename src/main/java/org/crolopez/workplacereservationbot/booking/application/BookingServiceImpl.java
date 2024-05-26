package org.crolopez.workplacereservationbot.booking.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.List;

@Singleton
public class BookingServiceImpl implements BookingService {

    @Inject
    BookingRepository bookingRepository;

    @Override
    public List<LocationEntity> getOfficeBookings() {
        return bookingRepository.getOfficeBookings();
    }

    @Override
    public List<LocationEntity> getParkingBookings() {
        return bookingRepository.getParkingBookings();
    }

    @Override
    public List<LocationEntity> getParkingAvailability(String date) {
        return bookingRepository.getParkingAvailability(date);
    }

    @Override
    public List<LocationEntity> getOfficeAvailability(String date) {
        return bookingRepository.getOfficeAvailability(date);
    }

    @Override
    public String bookParking(String date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate finalDate;

        if (date.matches("\\d+[dwm]")) {
            LocalDate currentDate = LocalDate.now();
            char timeUnit = date.charAt(date.length() - 1);
            int timeValue = Integer.parseInt(date.substring(0, date.length() - 1));

            switch (timeUnit) {
                case 'd':
                    finalDate = currentDate.plusDays(timeValue);
                    break;
                case 'w':
                    finalDate = currentDate.plusWeeks(timeValue);
                    break;
                case 'm':
                    finalDate = currentDate.plusMonths(timeValue);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid time unit in date offset");
            }
        } else {
            finalDate = LocalDate.parse(date, formatter);
        }

        String formattedDate = finalDate.format(formatter);
        return bookingRepository.bookParking(formattedDate);
    }
}
