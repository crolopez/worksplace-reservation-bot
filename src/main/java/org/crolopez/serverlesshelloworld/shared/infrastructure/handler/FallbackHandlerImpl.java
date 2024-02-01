package org.crolopez.serverlesshelloworld.shared.infrastructure.handler;

import jakarta.enterprise.context.ApplicationScoped;
import org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto.BaseOutputDto;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FallbackHandlerImpl implements FallbackHandler<BaseOutputDto> {
    private static final Logger logger = Logger.getLogger(FallbackHandlerImpl.class);

    @Override
    public BaseOutputDto handle(org.eclipse.microprofile.faulttolerance.ExecutionContext context) {
        String message = String.format("Unexpected error: %s", context.getFailure().getMessage());
        logger.error(message);
        return new BaseOutputDto().withResult(message);
    }
}