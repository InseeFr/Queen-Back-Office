package fr.insee.queen.application.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class FixedTimeConfiguration {

    @Bean
    @Primary
    public Clock clock() {
        Instant fixedInstant = Instant.ofEpochMilli(1747395350727L);
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        return Clock.fixed(fixedInstant, zoneId);
    }
}
