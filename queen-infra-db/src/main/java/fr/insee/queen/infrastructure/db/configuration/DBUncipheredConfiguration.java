package fr.insee.queen.infrastructure.db.configuration;

import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.data.CipheredDataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConditionalOnProperty(name = "feature.cipher.enabled", havingValue = "false")
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
        "fr.insee.queen.infrastructure.db.surveyunittempzone",
        "fr.insee.queen.infrastructure.db.surveyunit.entity",
        "fr.insee.queen.infrastructure.db.surveyunit.data.entity.unciphered"
})
public class DBUncipheredConfiguration {
}
