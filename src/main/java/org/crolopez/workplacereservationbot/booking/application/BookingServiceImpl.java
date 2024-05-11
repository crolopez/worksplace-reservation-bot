package org.crolopez.workplacereservationbot.booking.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;

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
    public String bookOfficeSlot() {
        return null;
    }

    @Override
    public String bookParkingSlot() {
        return null;
    }

    @Override
    public List<LocationEntity> getParkingAvailability(String date) {
        return bookingRepository.getParkingAvailability(date);
    }

    @Override
    public List<LocationEntity> getOfficeAvailability(String date) {
        return bookingRepository.getOfficeAvailability(date);
    }
}
