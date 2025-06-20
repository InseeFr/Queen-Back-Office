package fr.insee.queen.infrastructure.db.configuration;

import fr.insee.queen.infrastructure.db.data.repository.jpa.CipheredDataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConditionalOnProperty(name = "feature.sensitive-data.enabled", havingValue = "false")
@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "fr.insee.queen.infrastructure.db", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {CipheredDataJpaRepository.class}
        )
})
@EntityScan(basePackages = {
        "fr.insee.queen.infrastructure.db.campaign",
        "fr.insee.queen.infrastructure.db.paradata",
        "fr.insee.queen.infrastructure.db.interrogationtempzone",
        "fr.insee.queen.infrastructure.db.interrogation",
        "fr.insee.queen.infrastructure.db.surveyunittempzone",
        "fr.insee.queen.infrastructure.db.surveyunit",
        "fr.insee.queen.infrastructure.db.events",
        "fr.insee.queen.infrastructure.db.data.entity.common",
        "fr.insee.queen.infrastructure.db.data.entity.unciphered",
})
public class DBUncipheredConfiguration {
}
