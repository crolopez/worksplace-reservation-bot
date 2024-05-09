package org.crolopez.workplacereservationbot.shared.infrastructure.mapper;

public interface Mapper<EntityA, EntityB> {
    EntityB convert(EntityA entityA);
}
