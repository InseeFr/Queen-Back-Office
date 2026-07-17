package fr.insee.queen.jms.integration;

import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.service.InterrogationReplyQueuePublisher;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EnableJms
class InterrogationReplyQueuePublisherIT {

    private static final String REPLY_QUEUE = "reply.queue.it";

    /** Injecte dynamiquement la configuration Spring du broker embarqué */
    @Autowired
    private InterrogationReplyQueuePublisher publisher;

    @Autowired
    private JmsTemplate jmsTemplate; // pour la lecture côté test

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void send_should_publish_ObjectMessage_with_correlationId_and_json_payload() throws Exception {
        // Given
        UUID correlationId = UUID.randomUUID();
        JMSOutputMessage response = new JMSOutputMessage(200, "OK");

        // When
        publisher.send(REPLY_QUEUE, correlationId, response);

        // Then
        Message msg = jmsTemplate.receive(REPLY_QUEUE);
        assertThat(msg).as("Le message doit être présent dans la file").isNotNull();
        assertThat(msg.getJMSCorrelationID()).isEqualTo(correlationId.toString());
        assertThat(msg.getJMSDeliveryMode()).isEqualTo(DeliveryMode.PERSISTENT);

        assertThat(msg).isInstanceOf(ObjectMessage.class);
        String json = (String) ((ObjectMessage) msg).getObject();

        JsonNode root = jsonMapper.readTree(json);
        assertThat(root.get("code").asInt()).isEqualTo(200);
        assertThat(root.get("message").asString()).isEqualTo("OK");
    }
}
