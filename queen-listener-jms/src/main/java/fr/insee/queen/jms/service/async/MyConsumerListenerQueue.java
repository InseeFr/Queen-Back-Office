package fr.insee.queen.jms.service.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.bean.UniteEnquetee;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static fr.insee.queen.jms.configuration.ConfigurationJMS.UE_QUEUE;

@Slf4j
@Component
public class MyConsumerListenerQueue {

    //    @JmsListener est la seule annotation requise pour convertir une méthode d'un bean normal en un point de terminaison d'écoute JMS
    @JmsListener(destination = UE_QUEUE, containerFactory = "queueJmsListenerContainerFactory")
    public void queueConnectionFactory(Message message, Session session) throws JMSException, JsonProcessingException {
//    public void queueConnectionFactory(ActiveMQTextMessage ue) throws JMSException {
//        log.info("Received 1 <" + message + ">");
//        log.info("Received 2 <" + message.getClass() + ">");
//        log.info("Received 3 <" + message.getBody(String.class) + ">");

        ObjectMapper objectMapper = new ObjectMapper();
//        // Convert from json String to POJO
        UniteEnquetee yourPojo = objectMapper.readValue(message.getBody(String.class) , UniteEnquetee.class);
//        yourPojo.setReplyTo(yourPojo.getCorrelationID());

        log.info("Received yourPojo <" + yourPojo.toString() + ">");
//        log.info("Received yourPojo getCorrelationID <" + yourPojo.getCorrelationID() + ">");


        // Convert from POJO to json String
        ObjectMapper objectMapper2 = new ObjectMapper();
        String ueAsString = objectMapper2.writeValueAsString(yourPojo);


        final ObjectMessage responseMessage = new ActiveMQObjectMessage();
        responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
        responseMessage.setObject(ueAsString);

        final MessageProducer producer = session.createProducer(message.getJMSReplyTo());

        log.info("Launch to replyTo(queueConnectionFactory)");
        producer.send(responseMessage);
        log.info(yourPojo.toString());



    }

//    @JmsListener(destination = UE_REPLY_2_QUEUE, containerFactory = "queueJmsListenerContainerFactory")
    public void replyQueueConnectionFactory(UniteEnquetee ue) throws JMSException {
        log.info("ReplyTo <" + ue + ">");
    }


}