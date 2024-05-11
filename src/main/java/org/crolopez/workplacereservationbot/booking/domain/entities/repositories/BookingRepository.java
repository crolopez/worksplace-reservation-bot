package org.crolopez.workplacereservationbot.booking.domain.entities.repositories;

import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

public interface BookingRepository {

    List<LocationEntity> getParkingBookings();

    List<LocationEntity> getOfficeBookings();

    List<LocationEntity> getParkingAvailability(String date);

    List<LocationEntity> getOfficeAvailability(String date);
}
