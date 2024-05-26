package org.crolopez.workplacereservationbot.booking.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class LocationEntity {
    Date date;
    String id;
    String place;
    String space;
}
