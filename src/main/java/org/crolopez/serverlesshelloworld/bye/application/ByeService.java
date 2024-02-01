package org.crolopez.serverlesshelloworld.bye.application;

import jakarta.enterprise.context.ApplicationScoped;
import org.crolopez.serverlesshelloworld.shared.domain.UserEntity;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.BaseOutputDto;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ByeService {

    private static final Logger logger = Logger.getLogger(ByeService.class);

    public BaseOutputDto process(UserEntity user) {
        logger.info(String.format("Name: %s", user.getName()));
        return new BaseOutputDto().withResult(String.format("Bye %s!", user.getName()));
    }
}
