package fr.insee.queen.infrastructure.jms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "feature.multimode")
public class MultimodeProperties {

    private Publisher publisher = new Publisher();

    private Subscriber subscriber = new Subscriber();

    private String topic = "multimode_events";

    @Getter
    @Setter
    public static class Publisher {
        private boolean enabled = false;

        private Scheduler scheduler = new Scheduler();
    }

    @Getter
    @Setter
    public static class Subscriber {
        private boolean enabled = false;
    }

    @Getter
    @Setter
    public static class Scheduler {
        private long interval = 300000L; // 300 seconds in milliseconds
    }
}
