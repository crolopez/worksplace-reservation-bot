package org.crolopez.workplacereservationbot.telegram.application;

import org.crolopez.workplacereservationbot.booking.domain.entities.BookingEntity;

import java.util.List;

public interface TelegramFormatter {
    String format(List<BookingEntity> bookings);
}
