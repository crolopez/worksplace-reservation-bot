package org.crolopez.workplacereservationbot.telegram.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.SneakyThrows;
import org.crolopez.workplacereservationbot.shared.application.DispatcherService;
import org.crolopez.workplacereservationbot.shared.application.entities.command.EventEntity;
import org.crolopez.workplacereservationbot.shared.infrastructure.handler.FallbackHandlerImpl;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.telegram.dto.TelegramWebhookEventDto;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.dto.TelegramEventDto;
import org.crolopez.workplacereservationbot.shared.infrastructure.mapper.Mapper;
import org.crolopez.workplacereservationbot.telegram.infrastructure.repository.TelegramRepository;
import org.eclipse.microprofile.faulttolerance.Fallback;

@Named("telegram-handler")
public class TelegramHandler implements RequestHandler<TelegramEventDto, APIGatewayV2HTTPResponse> {

    @Inject
    DispatcherService dispatcher;

    @Inject
    TelegramRepository repository;

    @Inject
    Mapper<TelegramWebhookEventDto, EventEntity> inputMapper;

    @Inject
    Mapper<String, APIGatewayV2HTTPResponse> outputMapper;

    @SneakyThrows
    @Fallback(FallbackHandlerImpl.class)
    @Override
    public APIGatewayV2HTTPResponse handleRequest(TelegramEventDto requestEvent, Context context) {
        TelegramWebhookEventDto requestBody = requestEvent.getRequestBody();
        EventEntity eventEntity = inputMapper.convert(requestBody);

        String result = dispatcher.process(eventEntity);
        repository.send(requestBody.getMessage().getChat().getId(), result);

        return outputMapper.convert(result);
    }
}
