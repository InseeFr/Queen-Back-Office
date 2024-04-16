package fr.insee.queen.infrastructure.mongo.surveyunittempzone.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunittempzone.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;
import fr.insee.queen.infrastructure.mongo.surveyunittempzone.document.SurveyUnitTempZoneDocument;
import fr.insee.queen.infrastructure.mongo.surveyunittempzone.repository.SurveyUnitTempZoneMongoRepository;
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
public class SurveyUnitTempZoneMongoDao implements SurveyUnitTempZoneRepository {
    private final SurveyUnitTempZoneMongoRepository repository;

    @Override
    public void delete(String surveyUnitId) {
        repository.deleteBySurveyUnitId(surveyUnitId);
    }

    @Override
    public List<SurveyUnitTempZone> getAllSurveyUnits() {
        return repository.findAll().stream()
                .map(SurveyUnitTempZoneDocument::toModel)
                .toList();
    }

    @Override
    public void save(String surveyUnitId, String userId, Long date, ObjectNode surveyUnit) {
        SurveyUnitTempZoneDocument surveyUnitTempZone = SurveyUnitTempZoneDocument.fromModel(surveyUnitId, userId, date, surveyUnit);
        repository.save(surveyUnitTempZone);
    }
}
