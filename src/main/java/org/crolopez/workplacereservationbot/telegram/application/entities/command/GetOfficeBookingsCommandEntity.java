package org.crolopez.workplacereservationbot.telegram.application.entities.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.application.BookingService;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

@Singleton
public class GetOfficeBookingsCommandEntity extends TelegramCommandEntity {

    @Inject
    BookingService service;

    public GetOfficeBookingsCommandEntity() {
        this.alias = "/getOfficeBookings";
    }

    @Override
    protected String launch(String... args) {
        List<LocationEntity> bookings = service.getOfficeBookings();
        return formatter.format(bookings);
    }

    @Override
    protected int getExpectedArgs() {
        return 0;
    }
}


