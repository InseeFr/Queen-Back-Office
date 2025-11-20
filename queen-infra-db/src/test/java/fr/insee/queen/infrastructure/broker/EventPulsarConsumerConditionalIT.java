package fr.insee.queen.infrastructure.broker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventPulsarConsumerConditionalIT {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MessageConsumer testConsumer() {
            return new MessageConsumer() {
                @Override
                public boolean shouldConsume(String type) {
                    return true;
                }

                @Override
                public void consume(String type, BrokerMessage.Payload payload) {
                    // Test implementation
                }
            };
        }
    }

    @Nested
    @SpringBootTest(classes = {EventPulsarConsumer.class, TestConfig.class})
    @TestPropertySource(properties = "feature.cross-environment-communication.consumer=true")
    @DisplayName("When property is true")
    class WhenPropertyIsTrue {

        @Autowired
        private ApplicationContext context;

        @Test
        @DisplayName("Then EventPulsarConsumer bean is created")
        void testBeanCreated() {
            assertThat(context.containsBean("eventPulsarConsumer")).isTrue();
            EventPulsarConsumer consumer = context.getBean(EventPulsarConsumer.class);
            assertThat(consumer).isNotNull();
        }

        @Test
        @DisplayName("Then EventPulsarConsumer has access to MessageConsumers")
        void testConsumersInjected() {
            assertThat(context.containsBean("eventPulsarConsumer")).isTrue();
            assertThat(context.containsBean("testConsumer")).isTrue();

            EventPulsarConsumer pulsarConsumer = context.getBean(EventPulsarConsumer.class);
            assertThat(pulsarConsumer).isNotNull();
        }
    }

    @Nested
    @SpringBootTest(classes = {EventPulsarConsumer.class, TestConfig.class})
    @TestPropertySource(properties = "feature.cross-environment-communication.consumer=false")
    @DisplayName("When property is false")
    class WhenPropertyIsFalse {

        @Autowired
        private ApplicationContext context;

        @Test
        @DisplayName("Then EventPulsarConsumer bean is not created")
        void testBeanNotCreated() {
            assertThat(context.containsBean("eventPulsarConsumer")).isFalse();
            assertThatThrownBy(() -> context.getBean(EventPulsarConsumer.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Nested
    @SpringBootTest(classes = {EventPulsarConsumer.class, TestConfig.class})
    @DisplayName("When property is not set")
    class WhenPropertyNotSet {

        @Autowired
        private ApplicationContext context;

        @Test
        @DisplayName("Then EventPulsarConsumer bean is not created")
        void testBeanNotCreated() {
            assertThat(context.containsBean("eventPulsarConsumer")).isFalse();
            assertThatThrownBy(() -> context.getBean(EventPulsarConsumer.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }
}
