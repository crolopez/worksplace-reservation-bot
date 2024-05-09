package org.crolopez.workplacereservationbot.telegram.infrastructure.mapper;

import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.crolopez.workplacereservationbot.shared.application.entities.command.EventEntity;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.telegram.dto.TelegramWebhookEventDto;
import org.crolopez.workplacereservationbot.shared.infrastructure.mapper.Mapper;

import java.util.Arrays;

@Singleton
public class TelegramEventMapper implements Mapper<TelegramWebhookEventDto, EventEntity> {
    @SneakyThrows
    @Override
    public EventEntity convert(TelegramWebhookEventDto event) {
        String[] splitCommand = event.getMessage().getText().split(" ");
        return EventEntity.builder()
                .command(splitCommand[0])
                .args(Arrays.copyOfRange(splitCommand, 1, splitCommand.length))
                .build();
    }
}
