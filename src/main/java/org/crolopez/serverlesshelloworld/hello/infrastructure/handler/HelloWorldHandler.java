package org.crolopez.serverlesshelloworld.hello.infrastructure.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.SneakyThrows;
import org.crolopez.serverlesshelloworld.hello.application.HelloService;
import org.crolopez.serverlesshelloworld.shared.domain.UserEntity;
import org.crolopez.serverlesshelloworld.hello.infrastructure.handler.dto.HelloRequestDto;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.FallbackHandlerImpl;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.BaseOutputDto;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.UserDto;
import org.eclipse.microprofile.faulttolerance.Fallback;

@Named("hello-world")
public class HelloWorldHandler implements RequestHandler<HelloRequestDto, BaseOutputDto> {

    @Inject
    HelloService service;

    @SneakyThrows
    @Fallback(FallbackHandlerImpl.class)
    @Override
    public BaseOutputDto handleRequest(HelloRequestDto input, Context context) {
        UserDto userDto = input.getRequestBody();
        UserEntity userEntity = new UserEntity()
                .withName(userDto.getName());
        return service.process(userEntity)
                .withRequestId(context.getAwsRequestId());
    }
}
