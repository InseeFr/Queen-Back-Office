package fr.insee.queen;

import fr.insee.queen.jms.configuration.PropertiesLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication(scanBasePackages = "fr.insee.queen")
//@EntityScan("fr.insee.queen")
//@EnableJpaRepositories("fr.insee.queen")
@EnableCaching
@EnableJms
@Slf4j
public class JMSApplication {

        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(JMSApplication.class)
                        .listeners(new PropertiesLogger());
        }

        @EventListener
        public void handleApplicationReady(ApplicationReadyEvent event) {
                log.debug("=============== Queen Listener JMS has successfully started. ===============");
        }
}
