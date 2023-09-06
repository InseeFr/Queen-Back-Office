package fr.insee.queen.api.configuration;

import fr.insee.queen.api.service.DataSetInjectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@Slf4j
@Profile("dev")
@Configuration
public class DataInjectorOnStartup {

    private final DataSetInjectorService injector;

    public DataInjectorOnStartup(DataSetInjectorService injector) {
        this.injector = injector;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDataSetOnStartup() {
        injector.createDataSet();
    }
}
