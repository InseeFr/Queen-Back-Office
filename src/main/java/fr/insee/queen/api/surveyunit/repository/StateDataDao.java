package fr.insee.queen.api.surveyunit.repository;

import fr.insee.queen.api.surveyunit.repository.entity.StateDataDB;
import fr.insee.queen.api.surveyunit.repository.entity.SurveyUnitDB;
import fr.insee.queen.api.surveyunit.repository.jpa.StateDataJpaRepository;
import fr.insee.queen.api.surveyunit.repository.jpa.SurveyUnitJpaRepository;
import fr.insee.queen.api.surveyunit.service.gateway.StateDataRepository;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 */
@Repository
@AllArgsConstructor
public class StateDataDao implements StateDataRepository {

    private final StateDataJpaRepository jpaRepository;
    private final SurveyUnitJpaRepository surveyUnitJpaRepository;

    public Optional<StateData> find(String surveyUnitId) {
        return jpaRepository.findBySurveyUnitId(surveyUnitId);
    }

    public void update(String surveyUnitId, StateData stateData) {
        if (stateData == null) {
            return;
        }

        int countUpdated = jpaRepository.updateStateData(surveyUnitId, stateData.date(), stateData.currentPage(), stateData.state());
        if (countUpdated == 0) {
            create(surveyUnitId, stateData);
        }
    }

    public void create(String surveyUnitId, StateData stateData) {
        SurveyUnitDB surveyUnit = surveyUnitJpaRepository.getReferenceById(surveyUnitId);
        StateDataDB stateDataDB = new StateDataDB(UUID.randomUUID(), stateData.state(), stateData.date(), stateData.currentPage(), surveyUnit);
        jpaRepository.save(stateDataDB);
    }

    public boolean exists(String surveyUnitId) {
        return jpaRepository.existsBySurveyUnitId(surveyUnitId);
    }

    public void deleteBySurveyUnitId(String surveyUnitId) {
        jpaRepository.deleteBySurveyUnitId(surveyUnitId);
    }

    public void deleteStateDatas(String campaignId) {
        jpaRepository.deleteStateDatas(campaignId);
    }
}
