package org.crolopez.workplacereservationbot.booking.infrastructure.configuration;

import jakarta.inject.Inject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class BookingPlatformExecutor {

    @Inject
    BookingPlatformConfig config;

    @Produces
    @ApplicationScoped
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(config.bookingBehaviour().maxParallelBookings());
    }
}
