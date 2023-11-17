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
 * A survey unit is going to the temporary zone when an interviewer puts the survey unit while this survey unit is
 * not affected to the interviewer
 * In this case, problems are solved later ... (or not)
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
