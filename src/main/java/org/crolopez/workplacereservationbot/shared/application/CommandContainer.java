package org.crolopez.workplacereservationbot.shared.application;

import org.crolopez.workplacereservationbot.shared.application.entities.command.CommandEntity;

import java.util.List;

public interface CommandContainer {
    List<CommandEntity> getCommands();
}
