package fr.insee.queen.infrastructure.jms.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MultimodeProperties.class)
@EnableConfigurationProperties(MultimodeProperties.class)
@TestPropertySource(properties = {
        "feature.multimode.publisher.enabled=true",
        "feature.multimode.publisher.scheduler.interval=60000",
        "feature.multimode.topic=test_topic"
})
class MultimodePropertiesTest {

    @Autowired
    private MultimodeProperties multimodeProperties;

    @Test
    void shouldLoadMultimodeProperties() {
        assertThat(multimodeProperties).isNotNull();
        assertThat(multimodeProperties.getPublisher().isEnabled()).isTrue();
        assertThat(multimodeProperties.getTopic()).isEqualTo("test_topic");
    }

    @Test
    void shouldLoadSchedulerProperties() {
        assertThat(multimodeProperties.getPublisher().getScheduler()).isNotNull();
        assertThat(multimodeProperties.getPublisher().getScheduler().getInterval()).isEqualTo(60000L);
    }
}
