package fr.insee.queen.jms.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.bean.UniteEnquetee;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableJms
@EnableScheduling
@Configuration(enforceUniqueMethods = false)
public class ConfigurationJMS {

    public static final String UE_QUEUE_TEMPORAIRE = "queue-ue-temporaire";
    public static final String UE_QUEUE = "queue-ue";
    public static final String UE_REPLY_2_QUEUE = "reply-queue-ue";
    public static final String UE_TOPIC = "topic-ue";

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String user;

    @Value("${spring.activemq.password}")
    private String password;

//    @Bean
//    public MessageConverter jacksonJmsMessageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("_type");
//        return converter;
//    }

//    @Bean
//    public MessageConverter jacksonJmsMessageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTypeIdPropertyName("ue");
////        Map<String, Class<?>> typeIdMappings = new HashMap<String, Class<?>>();
////        typeIdMappings.put("JMS_TYPE", UniteEnquetee.class);
////        converter.setTypeIdPropertyName("_type");
////        converter.setTypeIdMappings(typeIdMappings);
//        converter.setTargetType(MessageType.TEXT);
////        converter.setTypeIdPropertyName("JMS_TYPE");
//
//        return converter;
//    }

//    @Bean
//    public MessageConverter jacksonJmsMessageConverter(){
////        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
//////        messageConverter.setObjectMapper(objectMapper);
////        messageConverter.setTargetType(MessageType.TEXT);
////
////        messageConverter.setTypeIdPropertyName("ichihedge.queue.json.classname");
////        //now set idMappings for serialization/deserialization
////        HashMap<String, Class<?>> idMapping = new HashMap<String, Class<?>>();
////        idMapping.put(UniteEnquetee.class.getName(), UniteEnquetee.class);
////        messageConverter.setTypeIdMappings(idMapping);
////
////        return messageConverter;
//        // Convert from POJO to json String
//
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("UniteEnquetee");
//
//        // Set up a map to convert our friendly message types to Java classes.
//        Map<String, Class<?>> typeIdMap = new HashMap<>();
//        typeIdMap.put("UniteEnquetee", UniteEnquetee.class);
//        converter.setTypeIdMappings(typeIdMap);
//
//        return converter;
//    }

    @Bean
    @Qualifier("queueConnectionFactory")
    public ConnectionFactory connectionFactory() {
        log.info("queueConnectionFactory");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.getUser(),this.getPassword(),this.getBrokerUrl());
//        connectionFactory.setTrustedPackages(Arrays.asList("fr.insee.sndil"));
        connectionFactory.setTrustAllPackages(true);
//        connectionFactory.setClientID(UUID.randomUUID().toString());
        return connectionFactory;
    }

    @Bean
    @Qualifier("topicConnectionFactory")
    public ConnectionFactory topicConnectionFactory() {
        log.info("topicConnectionFactory");
        ActiveMQConnectionFactory  connectionFactory = new ActiveMQConnectionFactory(this.getUser(),this.getPassword(),this.getBrokerUrl());
        connectionFactory.setTrustAllPackages(true);
//        connectionFactory.setClientID(UUID.randomUUID().toString());
        return connectionFactory;
    }


    @Bean
    @Primary
    public JmsTemplate jmstemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate(this.connectionFactory());
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean("JmsTemplateTemporaryQueue")
    @Qualifier
    public JmsTemplate JmsTemplateTemporaryQueue() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate(this.connectionFactory());
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean("JmsTemplateQueue")
    @Qualifier
    public JmsTemplate JmsTemplateQueue() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate(this.connectionFactory());
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean("JmsTemplateTopic")
    @Qualifier
    public JmsTemplate JmsTemplateTopic() throws JMSException {
        log.info("JmsTemplateTopic");
        JmsTemplate jmsTemplate = new JmsTemplate(this.topicConnectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.connectionFactory());
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueTemporarJmsListenerContainerFactory(@Qualifier("queueConnectionFactory") ConnectionFactory connectionFactory,
                                                                          DefaultJmsListenerContainerFactoryConfigurer configurer) {
        log.info("queueTemporarJmsListenerContainerFactory");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(false);
//        factory.setSubscriptionDurable(true);
//        factory.setClientId(UUID.randomUUID().toString());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        //factory.setConcurrency("1");
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(@Qualifier("queueConnectionFactory") ConnectionFactory connectionFactory,
                                                                 DefaultJmsListenerContainerFactoryConfigurer configurer) {
        log.info("queueJmsListenerContainerFactory");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(false);
//        factory.setMessageConverter(jacksonJmsMessageConverter());
//        factory.setSubscriptionDurable(true);
//        factory.setClientId(UUID.randomUUID().toString());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        //factory.setConcurrency("1");
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(@Qualifier("topicConnectionFactory") ConnectionFactory connectionFactory,
                                                                           DefaultJmsListenerContainerFactoryConfigurer configurer) {
        log.info("topicJmsListenerContainerFactory");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(topicConnectionFactory());
        factory.setPubSubDomain(true); // Set to true fo
//        factory.setSubscriptionDurable(true);// r topics
        return factory;
    }

//    @Bean
//    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory2(@Qualifier("topicConnectionFactory") ConnectionFactory connectionFactory,
//                                                                          DefaultJmsListenerContainerFactoryConfigurer configurer) {
//        log.info("topicJmsListenerContainerFactory");
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        configurer.configure(factory, connectionFactory);
//        factory.setConnectionFactory(topicConnectionFactory());
//        factory.setClientId(UUID.randomUUID().toString());
////        factory.setClientId("cliend_id");
//        factory.setPubSubDomain(true);
//        factory.setSubscriptionDurable(true);
//        factory.setReceiveTimeout(1000000L);
//        factory.setConcurrency("3-1000000");
//        factory.setRecoveryInterval(1000L);
////        factory.setConcurrency("1-1");
////        factory.setConcurrency( configurationHelper.getProperty(ConfigurationValue.JMS_LISTENER_POOL_CONCURRENCY_LIMITS));
////        factory.setConcurrency("1-2");
////        factory.setConcurrency("1");
//        return factory;
//    }

//    @Bean
//    public JmsListenerContainerFactory<?> jmsTopicListenerContainerFactory(ConnectionFactory connectionFactory) {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setPubSubDomain(true); // Set to true for topics
//        return factory;
//    }

    @Bean
    public Topic topic() {
        Topic topic = new ActiveMQTopic(UE_TOPIC);
        return topic;
    }


    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
