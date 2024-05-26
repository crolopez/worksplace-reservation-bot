package org.crolopez.workplacereservationbot.booking.infrastructure.entities;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginDetails {
    private String name;
    private String email;
    private String organisationId;
    private String personId;
    private String cookie;
}