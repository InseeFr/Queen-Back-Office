package fr.insee.queen.jms.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.service.InterrogationReplyQueuePublisher;
import jakarta.jms.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@EnableJms
class InterrogationReplyQueuePublisherIT extends AbstractIntegrationTest {

    private static final String REPLY_QUEUE = "reply.queue.it";

    @Autowired
    private InterrogationReplyQueuePublisher publisher;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @BeforeEach
    void setUp() throws JMSException {
        // Setup JMS consumer for the reply queue
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(REPLY_QUEUE);
        consumer = session.createConsumer(queue);
        connection.start();
    }

    @AfterEach
    void tearDown() throws JMSException {
        if (consumer != null) {
            consumer.close();
        }
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void send_should_publish_ObjectMessage_with_correlationId_and_json_payload() throws Exception {
        // Arrange
        String correlationId = UUID.randomUUID().toString();
        JMSOutputMessage response = new JMSOutputMessage(200, "OK");

        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<Message> receivedMessage = new AtomicReference<>();

        // Setup message listener
        consumer.setMessageListener(message -> {
            receivedMessage.set(message);
            messageLatch.countDown();
        });

        // Act
        publisher.send(REPLY_QUEUE, correlationId, response);

        // Assert: Wait for message to be received
        boolean messageReceived = messageLatch.await(10, TimeUnit.SECONDS);
        assertThat(messageReceived).as("Le message doit être reçu dans les 10 secondes").isTrue();

        Message msg = receivedMessage.get();
        assertThat(msg).as("Le message doit être présent dans la file").isNotNull();
        assertThat(msg.getJMSCorrelationID()).isEqualTo(correlationId);
        assertThat(msg.getJMSDeliveryMode()).isEqualTo(DeliveryMode.PERSISTENT);

        // Le publisher envoie un ObjectMessage contenant une String JSON
        assertThat(msg).isInstanceOf(ObjectMessage.class);
        String json = (String) ((ObjectMessage) msg).getObject();

        JsonNode root = objectMapper.readTree(json);
        assertThat(root.get("code").asInt()).isEqualTo(200);
        assertThat(root.get("message").asText()).isEqualTo("OK");
    }
}
