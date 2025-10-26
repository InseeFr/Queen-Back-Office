package fr.insee.queen.application.events.controller;

import fr.insee.queen.domain.events.service.EventsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EventsControllerConditionalTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(EventsService.class, () -> mock(EventsService.class))
            .withBean(EventsController.class);

    @Test
    @DisplayName("EventsController should be loaded when property is true")
    void testControllerLoadedWhenPropertyIsTrue() {
        contextRunner
                .withPropertyValues("feature.cross-environnement-communication.endpoint=true")
                .run(context -> {
                    assertThat(context).hasBean("eventsController");
                    assertThat(context.getBean(EventsController.class)).isNotNull();
                });
    }

    @Test
    @DisplayName("EventsController should NOT be loaded when property is false")
    void testControllerNotLoadedWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("feature.cross-environnement-communication.endpoint=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(EventsController.class);
                    assertThat(context).doesNotHaveBean("eventsController");
                });
    }

    @Test
    @DisplayName("EventsController should NOT be loaded when property is not set")
    void testControllerNotLoadedWhenPropertyIsNotSet() {
        contextRunner
                .run(context -> {
                    assertThat(context).doesNotHaveBean(EventsController.class);
                });
    }
}
