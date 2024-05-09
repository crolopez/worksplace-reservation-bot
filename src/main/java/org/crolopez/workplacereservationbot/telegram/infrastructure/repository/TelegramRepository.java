package org.crolopez.workplacereservationbot.telegram.infrastructure.repository;


import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.telegram.dto.TelegramMessageChatDto;

public interface TelegramRepository {
    void send(TelegramMessageChatDto chat, String message);
}
