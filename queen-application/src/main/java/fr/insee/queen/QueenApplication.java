package fr.insee.queen;

import fr.insee.queen.application.PropertiesLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(excludeFilters =
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fr.insee.queen.infrastructure.mongo.*")
)
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"fr.insee.queen.infrastructure.db"})
@ConfigurationPropertiesScan
@Slf4j
public class QueenApplication {

    public static void main(String[] args) {
        configure(new SpringApplicationBuilder()).build().run(args);
    }

    protected static SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(QueenApplication.class).listeners(new PropertiesLogger());
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        log.info("=============== Queen Back-Office has successfully started. ===============");
    }
}
