package fr.insee.queen.infrastructure.broker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class EventPulsarConsumerConditionalTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean("consumers", java.util.List.class, Collections::emptyList)
            .withBean(EventPulsarConsumer.class);

    @Test
    @DisplayName("EventPulsarConsumer should be loaded when property is true")
    void testConsumerLoadedWhenPropertyIsTrue() {
        contextRunner
                .withPropertyValues("feature.cross-environnement-communication.consummer=true")
                .run(context -> {
                    assertThat(context).hasBean("eventPulsarConsumer");
                    assertThat(context.getBean(EventPulsarConsumer.class)).isNotNull();
                });
    }

    @Test
    @DisplayName("EventPulsarConsumer should NOT be loaded when property is false")
    void testConsumerNotLoadedWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("feature.cross-environnement-communication.consummer=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(EventPulsarConsumer.class);
                    assertThat(context).doesNotHaveBean("eventPulsarConsumer");
                });
    }

    @Test
    @DisplayName("EventPulsarConsumer should NOT be loaded when property is not set")
    void testConsumerNotLoadedWhenPropertyIsNotSet() {
        contextRunner
                .run(context -> {
                    assertThat(context).doesNotHaveBean(EventPulsarConsumer.class);
                });
    }
}