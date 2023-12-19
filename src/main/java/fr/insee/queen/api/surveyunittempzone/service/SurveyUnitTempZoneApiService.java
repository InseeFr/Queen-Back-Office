package fr.insee.queen.api.surveyunittempzone.service;

import fr.insee.queen.api.surveyunittempzone.service.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitTempZoneApiService implements SurveyUnitTempZoneService {
    private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;

    @Override
    public void saveSurveyUnitToTempZone(String surveyUnitId, String userId, String surveyUnitData) {
        Long date = new Date().getTime();
        surveyUnitTempZoneRepository.save(surveyUnitId, userId, date, surveyUnitData);
    }

    @Override
    public List<SurveyUnitTempZone> getAllSurveyUnitTempZone() {
        return surveyUnitTempZoneRepository.getAllSurveyUnits();
    }
}
