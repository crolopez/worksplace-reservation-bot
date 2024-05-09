package org.crolopez.workplacereservationbot.booking.application;

import org.crolopez.workplacereservationbot.booking.domain.entities.BookingEntity;

import java.util.List;

public interface BookingService {
    List<BookingEntity> getOfficeBookings();
    List<BookingEntity> getParkingBookings();
    String bookOfficeSlot();
    String bookParkingSlot();
}
