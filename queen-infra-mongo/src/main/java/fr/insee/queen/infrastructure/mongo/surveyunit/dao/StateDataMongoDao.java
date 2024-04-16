package fr.insee.queen.infrastructure.mongo.surveyunit.dao;

import fr.insee.queen.domain.surveyunit.gateway.StateDataRepository;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.StateDataObject;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.SurveyUnitDocument;
import fr.insee.queen.infrastructure.mongo.surveyunit.repository.SurveyUnitMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO to handle state data
 */
@Repository
@RequiredArgsConstructor
public class StateDataMongoDao implements StateDataRepository {

    private final SurveyUnitMongoRepository surveyUnitRepository;

    @Override
    public Optional<StateData> find(String surveyUnitId) {
        return surveyUnitRepository.findStateData(surveyUnitId)
                .map(SurveyUnitDocument::getStateData)
                .map(StateDataObject::toModel);
    }

    @Override
    public void save(String surveyUnitId, StateData stateData) {
        if (stateData == null) {
            return;
        }
        surveyUnitRepository.saveStateData(surveyUnitId, StateDataObject.fromModel(stateData));
    }

    @Override
    public void create(String surveyUnitId, StateData stateData) {
        surveyUnitRepository.saveStateData(surveyUnitId, StateDataObject.fromModel(stateData));
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return surveyUnitRepository.existsStateDataBySurveyUnitId(surveyUnitId);
    }

}
