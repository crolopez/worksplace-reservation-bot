package org.crolopez.workplacereservationbot.telegram.infrastructure.configuration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "telegram")
public interface TelegramConfig {

    Api api();
    Bot bot();

    interface Bot {
        String defaultReportingChannel();

        String token();
    }

    interface Api {
        String hostname();
    }

}
