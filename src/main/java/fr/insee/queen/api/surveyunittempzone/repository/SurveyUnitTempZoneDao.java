package fr.insee.queen.api.surveyunittempzone.repository;

import fr.insee.queen.api.surveyunittempzone.repository.entity.SurveyUnitTempZoneDB;
import fr.insee.queen.api.surveyunittempzone.repository.jpa.SurveyUnitTempZoneJpaRepository;
import fr.insee.queen.api.surveyunittempzone.service.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to handle survey units in temp zone.
 * A survey unit going the temporary zone is a survey unit which cannot be found as a survey unit during an interviewer synchronisation
 * In this case and to handle the problem later, these kind of survey unit land to the temp zone.
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
    public void save(String surveyUnitId, String userId, Long date, String surveyUnit) {
        SurveyUnitTempZoneDB surveyUnitTempZone = new SurveyUnitTempZoneDB(surveyUnitId, userId, date, surveyUnit);
        jpaRepository.save(surveyUnitTempZone);
    }
}
