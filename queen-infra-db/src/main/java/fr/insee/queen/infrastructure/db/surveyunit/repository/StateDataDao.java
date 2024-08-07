package fr.insee.queen.infrastructure.db.surveyunit.repository;

import fr.insee.queen.domain.surveyunit.gateway.StateDataRepository;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.StateDataJpaRepository;
import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.SurveyUnitJpaRepository;
import fr.insee.queen.infrastructure.db.surveyunit.entity.StateDataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.SurveyUnitDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO to handle state data
 */
@Repository
@AllArgsConstructor
public class StateDataDao implements StateDataRepository {

    private final StateDataJpaRepository jpaRepository;
    private final SurveyUnitJpaRepository surveyUnitJpaRepository;

    @Override
    public Optional<StateData> find(String surveyUnitId) {
        return jpaRepository.findBySurveyUnitId(surveyUnitId);
    }

    @Override
    public void save(String surveyUnitId, StateData stateData) {
        if (stateData == null) {
            return;
        }

        int countUpdated = jpaRepository.updateStateData(surveyUnitId, stateData.date(), stateData.currentPage(), stateData.state());
        if (countUpdated == 0) {
            create(surveyUnitId, stateData);
        }
    }

    @Override
    public void create(String surveyUnitId, StateData stateData) {
        SurveyUnitDB surveyUnit = surveyUnitJpaRepository.getReferenceById(surveyUnitId);
        StateDataDB stateDataDB = new StateDataDB(stateData.state(), stateData.date(), stateData.currentPage(), surveyUnit);
        jpaRepository.save(stateDataDB);
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return jpaRepository.existsBySurveyUnitId(surveyUnitId);
    }

    /**
     * Delete state data for a survey unit
     * @param surveyUnitId survey unit id
     */
    public void deleteBySurveyUnitId(String surveyUnitId) {
        jpaRepository.deleteBySurveyUnitId(surveyUnitId);
    }

    /**
     * Delete all survey units state data for a campaign
     * @param campaignId campaign id
     */
    public void deleteStateDatas(String campaignId) {
        jpaRepository.deleteStateDatas(campaignId);
    }
}
