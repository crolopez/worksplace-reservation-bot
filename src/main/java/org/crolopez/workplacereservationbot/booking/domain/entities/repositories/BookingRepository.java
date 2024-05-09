package org.crolopez.workplacereservationbot.booking.domain.entities.repositories;

import org.crolopez.workplacereservationbot.booking.domain.entities.BookingEntity;

import java.util.List;

public interface BookingRepository {

    List<BookingEntity> getParkingBookings();

    List<BookingEntity> getOfficeBookings();
}
