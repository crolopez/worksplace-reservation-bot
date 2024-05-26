package org.crolopez.workplacereservationbot.telegram.infrastructure.repository;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.ApiClient;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.client.TelegramBotApi;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.client.dto.TelegramBotApiRequestDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
@Slf4j
public class TelegramRepositoryImpl implements TelegramRepository {

    @ConfigProperty(name = "telegram.bot.token")
    private String token;

    @ConfigProperty(name = "telegram.api.hostname")
    private String telegramApiHostname;

    private TelegramBotApi telegramBotApi;

    @PostConstruct
    public void init() {
        // The client cannot be injected: https://github.com/OpenAPITools/openapi-generator/blob/a5d3fb4f601e4bced19f39cb6d82799a71531282/modules/openapi-generator/src/main/resources/Java/libraries/native/api.mustache
        telegramBotApi = new TelegramBotApi(getApiClient());
    }

    @SneakyThrows
    @Override
    public void send(String chatId, String message) {
        TelegramBotApiRequestDto apiRequestDto = TelegramBotApiRequestDto.builder()
                .chatId(chatId)
                .text(message)
                .parseMode("markdown")
                .build();

        telegramBotApi.sendMessage(token, apiRequestDto);

        log.info(String.format("Message sent to %s chat: %s", chatId, message));
    }

    private ApiClient getApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setHost(telegramApiHostname);
        apiClient.setScheme("https");
        return apiClient;
    }
}
