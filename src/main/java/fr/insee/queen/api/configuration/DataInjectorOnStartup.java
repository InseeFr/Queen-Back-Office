package fr.insee.queen.api.configuration;

import fr.insee.queen.api.dataset.service.DataSetInjectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@ConditionalOnProperty(name = "feature.enableDataset", havingValue = "true")
@Configuration
@RequiredArgsConstructor
public class DataInjectorOnStartup {

    private final DataSetInjectorService injector;

    @EventListener(ApplicationReadyEvent.class)
    public void createDataSetOnStartup() {
        injector.createDataSet();
    }
}
