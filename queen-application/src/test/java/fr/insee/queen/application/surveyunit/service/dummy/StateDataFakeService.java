package fr.insee.queen.application.surveyunit.service.dummy;

import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.service.StateDataService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StateDataFakeService implements StateDataService {

    public static final String ERROR_MESSAGE = "error";
    @Getter
    private StateData stateDataSaved = null;

    @Setter
    private boolean isDateInvalid = false;

    private final Map<String, StateData> stateDatas;

    public StateDataFakeService() {
        stateDatas = new HashMap<>();
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT1_ID, new StateData(StateDataType.INIT, 123456789L, "1.0"));
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT2_ID, new StateData(StateDataType.VALIDATED, 12345678910L, "2.0"));
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT3_ID, new StateData(StateDataType.INIT, 1234567891011L, "3.0"));
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT4_ID, new StateData(StateDataType.VALIDATED, 1234567891011L, "3.0"));
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT5_ID, new StateData(StateDataType.EXTRACTED, 1234567891011L, "3.0"));
        stateDatas.put(SurveyUnitFakeService.SURVEY_UNIT6_ID, null);
    }

    @Override
    public StateData getStateData(String surveyUnitId) {
        return stateDatas.getOrDefault(surveyUnitId, null);
    }

    @Override
    public void saveStateData(String surveyUnitId, StateData stateData, boolean verifyDate) throws StateDataInvalidDateException {
        if(isDateInvalid) {
            throw new StateDataInvalidDateException(ERROR_MESSAGE);
        }
        stateDataSaved = stateData;
    }

    @Override
    public Optional<StateData> findStateData(String surveyUnitId) {
        return Optional.ofNullable(stateDatas.getOrDefault(surveyUnitId, null));
    }
}
