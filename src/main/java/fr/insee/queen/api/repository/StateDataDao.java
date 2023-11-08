package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.repository.entity.StateDataDB;
import fr.insee.queen.api.repository.entity.SurveyUnitDB;
import fr.insee.queen.api.repository.jpa.StateDataJpaRepository;
import fr.insee.queen.api.repository.jpa.SurveyUnitJpaRepository;
import fr.insee.queen.api.service.gateway.StateDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 *
 */
@Repository
@AllArgsConstructor
public class StateDataDao implements StateDataRepository {

    private final StateDataJpaRepository crudRepository;
    private final SurveyUnitJpaRepository surveyUnitJpaRepository;

    public Optional<StateDataDto> find(String surveyUnitId) {
        return crudRepository.findBySurveyUnitId(surveyUnitId);
    }

    public void update(String surveyUnitId, StateDataDto stateData) {
        crudRepository.updateStateData(surveyUnitId, stateData.date(), stateData.currentPage(), stateData.state());
    }

    public void create(String surveyUnitId, StateDataDto stateData) {
        SurveyUnitDB surveyUnit = surveyUnitJpaRepository.getReferenceById(surveyUnitId);
        StateDataDB stateDataDB = new StateDataDB(UUID.randomUUID(), stateData.state(), stateData.date(), stateData.currentPage(), surveyUnit);
        crudRepository.save(stateDataDB);
    }

    public boolean exists(String surveyUnitId) {
        return crudRepository.existsBySurveyUnitId(surveyUnitId);
    }
}
