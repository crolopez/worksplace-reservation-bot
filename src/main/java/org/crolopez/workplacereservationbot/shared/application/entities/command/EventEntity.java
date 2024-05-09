package org.crolopez.workplacereservationbot.shared.application.entities.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventEntity {
    private String command;
    private String[] args;
}
