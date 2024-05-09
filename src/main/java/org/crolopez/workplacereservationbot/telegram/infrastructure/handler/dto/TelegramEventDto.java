package org.crolopez.workplacereservationbot.telegram.infrastructure.handler.dto;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.telegram.infrastructure.handler.telegram.dto.TelegramWebhookEventDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
public class TelegramEventDto extends APIGatewayProxyRequestEvent  {

    public TelegramWebhookEventDto getRequestBody() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return this.getIsBase64Encoded()
            ? objectMapper.readValue(new ByteArrayInputStream(Base64.getDecoder().decode(this.getBody())), TelegramWebhookEventDto.class)
            : objectMapper.readValue(this.getBody(), TelegramWebhookEventDto.class);
    }
}