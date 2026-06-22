package fr.insee.queen.jms.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import tools.jackson.databind.cfg.DateTimeFeature;

import static tools.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;

@Configuration
public class MapperConfiguration {
    /**
     * Creates and configures a Jackson-based JMS message converter.
     * @return a {@link MessageConverter} configured for JSON serialization of JMS messages using Jackson
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    JsonMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
                .configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
                .configure(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true) // ISO-8601
                .build();
    }

}
