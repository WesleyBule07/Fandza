package com.fandza.ccs.Fandza.annotation;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.EnumSet;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

/**
 * Generates time-ordered UUID v7 values on insert.
 */
public class UuidV7Generator implements BeforeExecutionGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }

    @Override
    public Class<?> getGeneratedType() {
        return UUID.class;
    }

    @Override
    public Object generate(
            SharedSessionContractImplementor session,
            Object object,
            Object anything,
            EventType eventType) {
        return generate();
    }

    public UUID generate() {
        long timestamp = Instant.now().toEpochMilli();
        long msb = (timestamp & 0xFFFFFFFFFFFFL) << 16;
        msb |= 0x7000L;
        msb |= (random.nextLong() & 0x0FFFL);
        long lsb = (random.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }
}
