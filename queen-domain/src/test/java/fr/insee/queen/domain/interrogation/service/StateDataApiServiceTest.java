package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.infrastructure.dummy.StateDataFakeDao;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.model.StateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateDataApiServiceTest {

    private StateDataFakeDao stateDataDao;
    private StateDataApiService stateDataService;
    private final String interrogationId = "11";
    private final Clock fixedClock = Clock.fixed(
            Instant.ofEpochSecond(1740601599),
            ZoneId.systemDefault());

    @BeforeEach
    void init() {
        stateDataDao = new StateDataFakeDao();
        stateDataService = new StateDataApiService(stateDataDao, fixedClock);
    }

    @Test
    @DisplayName("On retrieving state data, get correct state data")
    void testGet01() {
        StateData stateData = stateDataService.getStateData(interrogationId);
        assertThat(stateData).isEqualTo(stateDataDao.getStateDataReturned());
    }

    @Test
    @DisplayName("On retrieving state data, when state data is empty, throw exception")
    void testGet02() {
        stateDataDao.setHasEmptyStateData(true);
        assertThatThrownBy(() -> stateDataService.getStateData(interrogationId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format(StateDataApiService.NOT_FOUND_MESSAGE, interrogationId));
    }

    @Test
    @DisplayName("On saving new state data, when previous state data doesn't exist, save new state data")
    void testSave01() throws StateDataInvalidDateException {
        stateDataDao.setHasEmptyStateData(true);
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, null, "5");
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false);
        StateData stateDataSaved = stateDataDao.getStateDataSaved();
        assertThat(stateDataSaved.date()).isEqualTo(Instant.now(fixedClock).toEpochMilli());
        assertThat(stateDataSaved.state()).isEqualTo(stateDataUpdate.state());
        assertThat(stateDataSaved.currentPage()).isEqualTo(stateDataUpdate.currentPage());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is older, save new state data")
    void testSave02() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        assertThat(stateDataUpdate.date()).isGreaterThan(stateDataDao.getStateDataReturned().date());
        stateDataService.saveStateData(interrogationId, stateDataUpdate, true);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is null, save new state data")
    void testSave02bis() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        stateDataDao.setStateDataReturned(new StateData(StateDataType.INIT, null, "2"));
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is newer, throw exception")
    void testSave03() {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(stateDataDao.getStateDataReturned().date());
        assertThatThrownBy(() -> stateDataService.saveStateData(interrogationId, stateDataUpdate, true))
                .isInstanceOf(StateDataInvalidDateException.class)
                .hasMessage(StateDataApiService.INVALID_DATE_MESSAGE);
        assertThat(stateDataDao.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is is newer, save new state data if verifyDate is false")
    void testSave04() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(stateDataDao.getStateDataReturned().date());
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate, false);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }
}
