package org.crolopez.workplacereservationbot.shared.infrastructure.Configuration;

import io.quarkus.runtime.annotations.RegisterForReflection;

// https://quarkus.io/guides/cache#going-native
@RegisterForReflection(classNames = { "com.github.benmanes.caffeine.cache.SSW", "com.github.benmanes.caffeine.cache.PSW" })
public class Reflections {
}
