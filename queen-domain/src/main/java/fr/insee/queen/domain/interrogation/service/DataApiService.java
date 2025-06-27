package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class DataApiService implements DataService {
    private final InterrogationRepository interrogationRepository;

    @Override
    public ObjectNode getData(String interrogationId) {
        return interrogationRepository
                .findData(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Data not found for interrogation %s", interrogationId)));
    }

    @Override
    @Transactional
    public void saveData(String interrogationId, ObjectNode dataValue) {
        interrogationRepository.saveData(interrogationId, dataValue);
    }

    @Override
    @Transactional
    public void updateCollectedData(String interrogationId, ObjectNode collectedData) {
        interrogationRepository.updateCollectedData(interrogationId, collectedData);
    }

    @Override
    @Transactional
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        interrogationRepository.cleanExtractedData(campaignId, startTimestamp, endTimestamp);
    }


}
