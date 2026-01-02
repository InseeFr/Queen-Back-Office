package fr.insee.queen.jms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    @Autowired
    @Qualifier("jmsQueuePublisher")
    private JmsTemplate jmsTemplate;

    public void sendTextMessage(String queue, String message) {
        jmsTemplate.send(queue, s -> s.createTextMessage(message));
    }
}
