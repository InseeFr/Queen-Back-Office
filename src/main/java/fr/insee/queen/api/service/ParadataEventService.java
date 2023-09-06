package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.ParadataEvent;
import fr.insee.queen.api.repository.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParadataEventService {

    private final ParadataEventRepository paradataEventRepository;

    public void save(String paradataValue) {
        ParadataEvent paradataEvent = new ParadataEvent();
        paradataEvent.value(paradataValue);
        paradataEventRepository.save(paradataEvent);
    }

    public void save(ParadataEvent paradataEvent) {
        paradataEventRepository.save(paradataEvent);
    }
}
