package org.crolopez.workplacereservationbot.booking.infrastructure.configuration;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "bookingPlatform")
public interface BookingPlatformConfig {
    String hostname();

    Login login();

    MatchingTag matchingTag();

    List<BookingPreference> bookingPreferences();

    Keys keys();

    Schedule schedule();

    BookingBehaviour bookingBehaviour();

    interface BookingBehaviour {
        Integer maxParallelBookings();
        Integer bookTimeout();
    }

    interface Schedule {
        Parking parking();
    }

    interface Parking {
        String offset();
    }

    interface Login {

        String user();

        String password();
    }

    interface MatchingTag {
        String office();

        String parking();
    }

    interface Keys {
        KeyNode office();

        KeyNode parking();
    }

    interface KeyNode {
        String bc();

        String l();
    }


    interface BookingPreference {
        String space();

        List<String> priority();
    }

}
