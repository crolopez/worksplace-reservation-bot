package org.crolopez.workplacereservationbot.telegram.application.entities.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.booking.application.BookingService;
import org.crolopez.workplacereservationbot.booking.domain.entities.LocationEntity;

import java.util.List;

@Singleton
public class GetParkingAvailabilityCommandEntity extends TelegramCommandEntity {

    @Inject
    BookingService service;

    public GetParkingAvailabilityCommandEntity() {
        this.alias = "/getParkingAvailability";
    }

    @Override
    protected String launch(String... args) {
        List<LocationEntity> parkingList = service.getParkingAvailability(args[0]);
        return formatter.format(parkingList);
    }

    @Override
    protected int getExpectedArgs() {
        return 1;
    }
}


