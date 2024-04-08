package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.infrastructure.dummy.StateDataFakeDao;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.model.StateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateDataApiServiceTest {

    private StateDataFakeDao stateDataDao;
    private StateDataApiService stateDataService;
    private final String surveyUnitId = "11";

    @BeforeEach
    void init() {
        stateDataDao = new StateDataFakeDao();
        stateDataService = new StateDataApiService(stateDataDao);
    }

    @Test
    @DisplayName("On retrieving state data, get correct state data")
    void testGet01() {
        StateData stateData = stateDataService.getStateData(surveyUnitId);
        assertThat(stateData).isEqualTo(stateDataDao.getStateDataReturned());
    }

    @Test
    @DisplayName("On retrieving state data, when state data is empty, throw exception")
    void testGet02() {
        stateDataDao.setHasEmptyStateData(true);
        assertThatThrownBy(() -> stateDataService.getStateData(surveyUnitId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format(StateDataApiService.NOT_FOUND_MESSAGE, surveyUnitId));
    }

    @Test
    @DisplayName("On saving new state data, when previous state data doesn't exist, save new state data")
    void testSave01() throws StateDataInvalidDateException {
        stateDataDao.setHasEmptyStateData(true);
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 1000000L, "5");
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is older, save new state data")
    void testSave02() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        assertThat(stateDataUpdate.date()).isGreaterThan(stateDataDao.getStateDataReturned().date());
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is null, save new state data")
    void testSave02bis() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        stateDataDao.setStateDataReturned(new StateData(StateDataType.INIT, null, "2"));
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is newer, throw exception")
    void testSave03() {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(stateDataDao.getStateDataReturned().date());
        assertThatThrownBy(() -> stateDataService.saveStateData(surveyUnitId, stateDataUpdate))
                .isInstanceOf(StateDataInvalidDateException.class)
                .hasMessage(StateDataApiService.INVALID_DATE_MESSAGE);
        assertThat(stateDataDao.getStateDataSaved()).isNull();
    }
}
