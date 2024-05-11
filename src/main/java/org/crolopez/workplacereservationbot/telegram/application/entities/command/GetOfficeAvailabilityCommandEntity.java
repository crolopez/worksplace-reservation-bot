package org.crolopez.workplacereservationbot.telegram.application.entities.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.application.BookingService;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

@Singleton
public class GetOfficeAvailabilityCommandEntity extends TelegramCommandEntity {

    @Inject
    BookingService service;

    public GetOfficeAvailabilityCommandEntity() {
        this.alias = "/getOfficeAvailability";
    }

    @Override
    protected String launch(String... args) {
        List<LocationEntity> officeSlots = service.getOfficeAvailability(args[0]);
        return formatter.format(officeSlots);
    }

    @Override
    protected int getExpectedArgs() {
        return 1;
    }
}


