package fr.insee.queen.api.surveyunittempzone.repository;

import fr.insee.queen.api.surveyunittempzone.repository.jpa.SurveyUnitTempZoneJpaRepository;
import fr.insee.queen.api.surveyunittempzone.service.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
 *
 * @author Laurent Caouissin
 */
@Repository
@AllArgsConstructor
public class SurveyUnitTempZoneDao implements SurveyUnitTempZoneRepository {
    private final SurveyUnitTempZoneJpaRepository jpaRepository;

    @Override
    public void delete(String surveyUnitId) {
        jpaRepository.deleteBySurveyUnitId(surveyUnitId);
    }

    @Override
    public List<SurveyUnitTempZone> getAllSurveyUnits() {
        return jpaRepository.findAllProjectedBy();
    }

    @Override
    public void save(UUID id, String surveyUnitId, String userId, Long date, String surveyUnit) {
        jpaRepository.saveSurveyUnit(id, surveyUnitId, userId, date, surveyUnit);
    }
}
