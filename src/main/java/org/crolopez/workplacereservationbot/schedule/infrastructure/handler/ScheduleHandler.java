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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

        // Launch the command when it is not possible to book just for initialize the cache
        dispatcher.process(eventEntity);

        long waitingTime = getWaitingTime();
        log.info("Waiting {}ms", waitingTime);
        try {
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Sleep interrupted", e);
        }
        log.info("Launching the scheduled command");

        String result;
        do {
            result = dispatcher.process(eventEntity);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while(result.contains("Could not"));

        repository.send(result);

        return outputMapper.convert(result);
    }

    private long getWaitingTime() {
        Instant now = Instant.now();
        Instant nextMinute = now.plus(1, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);
        return ChronoUnit.MILLIS.between(now, nextMinute) + 10; // Little offset
    }

}
