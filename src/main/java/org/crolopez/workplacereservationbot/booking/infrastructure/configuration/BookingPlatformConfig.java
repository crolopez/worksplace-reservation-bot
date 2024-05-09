package org.crolopez.workplacereservationbot.booking.infrastructure.configuration;

import io.smallrye.config.ConfigMapping;
import jakarta.inject.Singleton;
import lombok.Data;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigMapping(prefix = "bookingPlatform")
public interface BookingPlatformConfig {
    String hostname();

    Login login();

    MatchingTag matchingTag();

    interface Login {

        String user();

        String password();
    }

    interface MatchingTag {
        String office();

        String parking();
    }
}
