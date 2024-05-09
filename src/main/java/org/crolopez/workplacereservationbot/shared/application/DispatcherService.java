package org.crolopez.workplacereservationbot.shared.application;

import org.crolopez.workplacereservationbot.shared.application.entities.command.EventEntity;

public interface DispatcherService {
    String process(EventEntity eventEntity);
}
