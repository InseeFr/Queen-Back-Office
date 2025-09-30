package fr.insee.queen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "fr.insee.queen")
@EnableTransactionManagement
@ConfigurationPropertiesScan
@EnableCaching
@EnableJms
@Slf4j
public class JMSApplication {

        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(JMSApplication.class)
                        .listeners();
        }

        @EventListener
        public void handleApplicationReady(ApplicationReadyEvent event) {
                log.info("=============== Queen listener JMS has successfully started. ===============");
        }
}
