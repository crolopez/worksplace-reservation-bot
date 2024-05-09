package org.crolopez.workplacereservationbot.booking.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.domain.entities.BookingEntity;
import org.crolopez.workplacereservationbot.booking.domain.entities.repositories.BookingRepository;

import java.util.List;

@Singleton
public class BookingServiceImpl implements BookingService {

    @Inject
    BookingRepository bookingRepository;

    @Override
    public List<BookingEntity> getOfficeBookings() {
        return bookingRepository.getOfficeBookings();
    }

    @Override
    public List<BookingEntity> getParkingBookings() {
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
}
