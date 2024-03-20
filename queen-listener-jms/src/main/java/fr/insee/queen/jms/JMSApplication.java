package fr.insee.queen.jms;

import fr.insee.queen.jms.configuration.PropertiesLogger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class JMSApplication {

        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(JMSApplication.class)
                        .listeners(new PropertiesLogger());
        }
}
