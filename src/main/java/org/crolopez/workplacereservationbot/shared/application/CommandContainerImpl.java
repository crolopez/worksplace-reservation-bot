package org.crolopez.workplacereservationbot.shared.application;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.crolopez.workplacereservationbot.shared.application.entities.command.CommandEntity;

import jakarta.inject.Inject;
import org.crolopez.workplacereservationbot.telegram.application.entities.command.GetOfficeAvailabilityCommandEntity;
import org.crolopez.workplacereservationbot.telegram.application.entities.command.GetOfficeBookingsCommandEntity;
import org.crolopez.workplacereservationbot.telegram.application.entities.command.GetParkingAvailabilityCommandEntity;
import org.crolopez.workplacereservationbot.telegram.application.entities.command.GetParkingBookingsCommandEntity;

import java.util.List;

@Singleton
public class CommandContainerImpl {

    @Getter
    private List<CommandEntity> commands;

    @Inject
    private GetOfficeBookingsCommandEntity getOfficeBookingsCommand;

    @Inject
    private GetParkingBookingsCommandEntity getParkingBookingsCommand;

    @Inject
    private GetOfficeAvailabilityCommandEntity getOfficeAvailabilityCommand;

    @Inject
    private GetParkingAvailabilityCommandEntity getParkingAvailabilityCommand;

    @PostConstruct
    private void init() {
        this.commands = List.of(
                getOfficeBookingsCommand,
                getParkingBookingsCommand,
                getOfficeAvailabilityCommand,
                getParkingAvailabilityCommand
        );
    }
}
