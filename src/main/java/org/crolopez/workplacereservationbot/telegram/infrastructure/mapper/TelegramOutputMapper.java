package org.crolopez.workplacereservationbot.telegram.infrastructure.mapper;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crolopez.workplacereservationbot.shared.infrastructure.mapper.Mapper;

@Singleton
public class TelegramOutputMapper implements Mapper<String, APIGatewayV2HTTPResponse> {
    @Override
    public APIGatewayV2HTTPResponse convert(String result) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody("{}") // TODO
                .build();
    }
}

