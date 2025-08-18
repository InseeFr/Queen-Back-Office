package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalizationApiService implements PersonalizationService {
    private final InterrogationRepository interrogationRepository;

    @Override
    public ArrayNode getPersonalization(String interrogationId) {
        return interrogationRepository
                .findPersonalization(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Personalization not found for interrogation %s", interrogationId)));
    }

    @Override
    public void updatePersonalization(String interrogationId, ArrayNode personalizationValue) {
        interrogationRepository.savePersonalization(interrogationId, personalizationValue);
    }
}
