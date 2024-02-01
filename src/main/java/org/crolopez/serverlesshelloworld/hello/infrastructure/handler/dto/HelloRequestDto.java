package org.crolopez.serverlesshelloworld.hello.infrastructure.handler.dto;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.UserDto;

public class HelloRequestDto extends APIGatewayProxyRequestEvent  {
    public UserDto getRequestBody() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.getBody(), UserDto.class);
    }
}
