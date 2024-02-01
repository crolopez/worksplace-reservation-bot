package org.crolopez.serverlesshelloworld.bye.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import org.crolopez.serverlesshelloworld.bye.application.ByeService;
import org.crolopez.serverlesshelloworld.bye.infrastructure.handler.dto.ByeRequestDto;
import org.crolopez.serverlesshelloworld.shared.domain.UserEntity;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.FallbackHandlerImpl;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.BaseOutputDto;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.UserDto;
import org.eclipse.microprofile.faulttolerance.Fallback;

@Named("bye-world")
public class ByeWorldHandler implements RequestHandler<ByeRequestDto, BaseOutputDto> {

    @Inject
    ByeService service;

    @SneakyThrows
    @Fallback(FallbackHandlerImpl.class)
    @Override
    public BaseOutputDto handleRequest(ByeRequestDto input, Context context) {
        UserDto userDto = input.getRequestBody();
        UserEntity userEntity = new UserEntity()
                .withName(userDto.getName());
        return service.process(userEntity)
                .withRequestId(context.getAwsRequestId());
    }
}
