package org.crolopez.workplacereservationbot.schedule.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.booking.infrastructure.configuration.BookingPlatformConfig;
import org.crolopez.workplacereservationbot.shared.application.DispatcherService;
import org.crolopez.workplacereservationbot.shared.application.entities.command.EventEntity;
import org.crolopez.workplacereservationbot.shared.infrastructure.mapper.Mapper;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.telegram.dto.TelegramWebhookEventDto;
import org.crolopez.workplacereservationbot.telegram.infrastructure.repository.TelegramRepository;

import java.util.Arrays;

@Slf4j
@Named("schedule-handler")
public class ScheduleHandler implements RequestHandler<ScheduledEvent, APIGatewayV2HTTPResponse> {

    @Inject
    private BookingPlatformConfig config;

    @Inject
    DispatcherService dispatcher;

    @Inject
    TelegramRepository repository;

    @Inject
    Mapper<TelegramWebhookEventDto, EventEntity> inputMapper;

    @Inject
    Mapper<String, APIGatewayV2HTTPResponse> outputMapper;

    @Override
    public APIGatewayV2HTTPResponse handleRequest(ScheduledEvent scheduledEvent, Context context) {
        String[] splitCommand = "/bookParking 2w".split(" "); // TOIMPROVE | DO IT WITH A MAPPER
        EventEntity eventEntity = EventEntity.builder()
                .command(splitCommand[0])
                .args(Arrays.copyOfRange(splitCommand, 1, splitCommand.length))
                .build();

        String result = dispatcher.process(eventEntity);
        repository.send("13317539", result);

        return outputMapper.convert(result);
    }

}
