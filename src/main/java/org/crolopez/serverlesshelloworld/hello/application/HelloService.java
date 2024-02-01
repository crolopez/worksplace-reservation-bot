package org.crolopez.serverlesshelloworld.hello.application;

import jakarta.enterprise.context.ApplicationScoped;
import org.crolopez.serverlesshelloworld.shared.domain.UserEntity;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.BaseOutputDto;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HelloService {

    private static final Logger logger = Logger.getLogger(HelloService.class);

    public BaseOutputDto process(UserEntity user) {
        logger.info(String.format("Name: %s", user.getName()));
        return new BaseOutputDto().withResult(String.format("Hello %s!", user.getName()));
    }
}
