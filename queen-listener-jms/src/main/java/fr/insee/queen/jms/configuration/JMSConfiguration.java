package fr.insee.queen.jms.configuration;

import com.datastax.oss.pulsar.jms.PulsarConnectionFactory;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@EnableJms
@EnableScheduling
@Configuration(enforceUniqueMethods = false)
public class JMSConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "broker",
            name = "name",
            havingValue = "artemis",
            matchIfMissing = false
    )
    public JmsListenerContainerFactory<?> jmsListenerFactory(ConnectionFactory connectionFactory,
                                                             DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all auto-configured defaults to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some settings if necessary.
        return factory;
    }

//    @Bean
//    @ConditionalOnProperty(
//            prefix = "broker",
//            name = "name",
//            havingValue = "pulsar",
//            matchIfMissing = false
//    )
//    public ConnectionFactory pulsarConnectionFactory(CustomProperties props) {
//        Map<String,Object> conf = new HashMap<>();
//        conf.put("brokerServiceUrl", props.getBrokerServiceUrl());
//        conf.put("webServiceUrl", props.getWebServiceUrl());
//        conf.put("enableTransaction", false); //
//        return new PulsarConnectionFactory(conf);
//    }
//
//    @Bean
//    @ConditionalOnProperty(
//            prefix = "broker",
//            name = "name",
//            havingValue = "pulsar",
//            matchIfMissing = false
//    )
//    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory cf) {
//        DefaultJmsListenerContainerFactory f = new DefaultJmsListenerContainerFactory();
//        f.setConnectionFactory(cf);
//        f.setConcurrency("1-3");
//        f.setSessionTransacted(false);
//        // f.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE); // si ack manuel
//        return f;
//    }
}
