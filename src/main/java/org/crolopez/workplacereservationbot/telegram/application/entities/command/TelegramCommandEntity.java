package org.crolopez.workplacereservationbot.telegram.application.entities.command;

import jakarta.inject.Inject;
import lombok.Getter;
import org.crolopez.workplacereservationbot.shared.application.entities.command.CommandEntity;
import org.crolopez.workplacereservationbot.telegram.application.TelegramFormatter;

@Getter
public abstract class TelegramCommandEntity extends CommandEntity {
    @Inject
    protected TelegramFormatter formatter;
}
