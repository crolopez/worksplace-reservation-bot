package org.crolopez.workplacereservationbot.telegram.application;

import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

public interface TelegramFormatter {
    String format(List<LocationEntity> bookings);
}
