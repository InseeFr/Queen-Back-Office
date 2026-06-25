package fr.insee.queen.jms.configuration;

import fr.insee.queen.domain.group.gateway.GroupKindProvider;
import fr.insee.queen.domain.group.model.GroupKind;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupKindConfiguration {
    /**
     * JMS is only used with partitions
     * @return group kind provider
     */
    @Bean
    public GroupKindProvider groupKindProvider() {
        return () -> GroupKind.PARTITION;
    }
}
