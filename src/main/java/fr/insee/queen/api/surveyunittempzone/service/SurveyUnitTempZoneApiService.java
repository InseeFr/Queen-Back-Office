package fr.insee.queen.api.surveyunittempzone.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.surveyunittempzone.service.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitTempZoneApiService implements SurveyUnitTempZoneService {
    private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;

    @Override
    public void saveSurveyUnitToTempZone(String surveyUnitId, String userId, JsonNode surveyUnit) {
        Long date = new Date().getTime();
        UUID id = UUID.randomUUID();
        surveyUnitTempZoneRepository.save(id, surveyUnitId, userId, date, surveyUnit.toString());
    }

    @Override
    public List<SurveyUnitTempZone> getAllSurveyUnitTempZone() {
        return surveyUnitTempZoneRepository.getAllSurveyUnits();
    }
}
