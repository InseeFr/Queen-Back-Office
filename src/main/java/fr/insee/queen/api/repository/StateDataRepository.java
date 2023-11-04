package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.entity.StateDataDB;
import fr.insee.queen.api.entity.SurveyUnitDB;
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
public class StateDataRepository {

    private final StateDataCrudRepository crudRepository;
    private final SurveyUnitCrudRepository surveyUnitCrudRepository;

    public Optional<StateDataDto> findBySurveyUnitId(String surveyUnitId) {
        return crudRepository.findBySurveyUnitId(surveyUnitId);
    }

    public void updateStateData(String surveyUnitId, StateDataDto stateData) {
        SurveyUnitDB surveyUnit = surveyUnitCrudRepository.getReferenceById(surveyUnitId);
        StateDataDB stateDataDB = crudRepository.getBySurveyUnitId(surveyUnitId);
        stateDataDB.state(stateData.state());
        stateDataDB.date(stateData.date());
        stateDataDB.currentPage(stateData.currentPage());
        stateDataDB.surveyUnit(surveyUnit);
        crudRepository.save(stateDataDB);
    }

    public void deleteStateDatas(String campaignId) {
        crudRepository.deleteStateDatas(campaignId);
    }

    public void createStateData(String surveyUnitId, StateDataDto stateData) {
        SurveyUnitDB surveyUnit = surveyUnitCrudRepository.getReferenceById(surveyUnitId);
        StateDataDB stateDataDB = new StateDataDB(UUID.randomUUID(), stateData.state(), stateData.date(), stateData.currentPage(), surveyUnit);
        crudRepository.save(stateDataDB);
    }

    public void createStateData() {
        StateDataDB stateDataDB = new StateDataDB();
        crudRepository.save(stateDataDB);
    }

    public boolean existsBySurveyUnitId(String surveyUnitId) {
            return crudRepository.existsBySurveyUnitId(surveyUnitId);
    }
}
