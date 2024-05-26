package org.crolopez.workplacereservationbot.telegram.infrastructure.repository;


public interface TelegramRepository {
    void send(String chatId, String message);
}
