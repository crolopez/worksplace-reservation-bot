package org.crolopez.workplacereservationbot.telegram.application;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class TelegramFormatterImpl implements TelegramFormatter {
    @Override
    public String format(List<LocationEntity> bookings) {
        if (bookings.isEmpty())
            return "Cannot find incoming bookings";

        return bookings.stream()
                .map(booking -> String.format("*Date*: %s\n*Place*: %s", booking.getDate(), booking.getPlace()))
                .collect(Collectors.joining("\n----\n"));
    }
}
