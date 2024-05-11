package org.crolopez.workplacereservationbot.booking.application;

import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

public interface BookingService {
    List<LocationEntity> getOfficeBookings();
    List<LocationEntity> getParkingBookings();
    String bookOfficeSlot();
    String bookParkingSlot();
    List<LocationEntity> getParkingAvailability(String date);
    List<LocationEntity> getOfficeAvailability(String date);
}
