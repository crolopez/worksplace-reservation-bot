package org.crolopez.workplacereservationbot.telegram.application.entities.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.application.BookingService;

@Singleton
public class BookParkingCommandEntity extends TelegramCommandEntity {

    @Inject
    BookingService service;

    public BookParkingCommandEntity() {
        this.alias = "/bookParking";
    }

    @Override
    protected String launch(String... args) {
        return service.bookParking(args[0]);
    }

    @Override
    protected int getExpectedArgs() {
        return 1;
    }
}


