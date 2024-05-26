package org.crolopez.workplacereservationbot.shared.application;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.crolopez.workplacereservationbot.shared.application.entities.command.CommandEntity;

import jakarta.inject.Inject;
import org.crolopez.workplacereservationbot.telegram.application.entities.command.*;

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

    @Inject
    private BookParkingCommandEntity bookParkingCommand;

    @PostConstruct
    private void init() {
        this.commands = List.of(
                getOfficeBookingsCommand,
                getParkingBookingsCommand,
                getOfficeAvailabilityCommand,
                getParkingAvailabilityCommand,
                bookParkingCommand
        );
    }
}
