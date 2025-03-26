package fr.insee.queen.domain.paradata.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.paradata.gateway.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParadataEventApiService implements ParadataEventService {

    private final ParadataEventRepository paradataEventRepository;
    private final InterrogationService interrogationService;

    @Override
    public void createParadataEvent(String interrogationId, ObjectNode paradataValue) {
        InterrogationSummary interrogationSummary = interrogationService.getSummaryById(interrogationId);
        paradataEventRepository.createParadataEvent(interrogationSummary, paradataValue);
    }
}
