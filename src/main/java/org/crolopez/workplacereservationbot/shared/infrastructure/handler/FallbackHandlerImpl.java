package org.crolopez.workplacereservationbot.shared.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.shared.infrastructure.mapper.Mapper;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class FallbackHandlerImpl implements FallbackHandler<APIGatewayV2HTTPResponse> {

    @Inject
    Mapper<String, APIGatewayV2HTTPResponse> outputMapper;

    @Override
    public APIGatewayV2HTTPResponse handle(org.eclipse.microprofile.faulttolerance.ExecutionContext context) {
        String message = String.format("Unexpected error: %s", context.getFailure().getMessage());
        String errorStackTrace = Arrays.stream(context.getFailure().getStackTrace()).map(x -> x.toString())
                .collect(Collectors.joining("\n"));
        log.error(String.format("%s. Stacktrace: \n%s", message, errorStackTrace));

        return outputMapper.convert(message);
    }
}