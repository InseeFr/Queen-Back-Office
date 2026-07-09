package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.infrastructure.dummy.StateDataFakeDao;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false, false);
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
        stateDataService.saveStateData(interrogationId, stateDataUpdate, true, false);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is null, save new state data")
    void testSave02bis() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        stateDataDao.setStateDataReturned(new StateData(StateDataType.INIT, null, "2"));
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false, false);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is newer, throw exception")
    void testSave03() {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(stateDataDao.getStateDataReturned().date());
        assertThatThrownBy(() -> stateDataService.saveStateData(interrogationId, stateDataUpdate, true, false))
                .isInstanceOf(StateDataInvalidDateException.class)
                .hasMessage(StateDataApiService.INVALID_DATE_MESSAGE);
        assertThat(stateDataDao.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is is newer, save new state data if verifyDate is false")
    void testSave04() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(stateDataDao.getStateDataReturned().date());
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false, false);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @ParameterizedTest
    @ValueSource(strings = { "VALIDATED", "EXTRACTED"})
    @DisplayName("On saving new state data, when previous state is VALIDATED or EXTRACTED, throw error")
    void testSaveInvalidTransitionState(String stateData) {
        // Given
        stateDataDao.setStateDataReturned(new StateData(StateDataType.valueOf(stateData), 800000L, "validationPage"));
        StateData stateDataUpdate = new StateData(StateDataType.INIT, 800000L, "5");
        // When
        Executable executable = () -> stateDataService.saveStateData(interrogationId, stateDataUpdate, false, true);
        // Then
        assertThrows(StateDataInvalidTransitionException.class, executable);
    }

    @Test
    @DisplayName("On saving new state data, when previous state is other than VALIDATED or EXTRACT, don't throw error")
    void testSaveInvalidTransitionState() {
        // Given
        stateDataDao.setStateDataReturned(new StateData(StateDataType.INIT, 800001L, "welcomePage"));
        StateData stateDataUpdate = new StateData(StateDataType.INIT, 800000L, "5");
        // When
        Executable executable = () -> stateDataService.saveStateData(interrogationId, stateDataUpdate, false, true);
        // Then
        assertDoesNotThrow(executable);
    }

    @Test
    @DisplayName("On saving new state data, when previous state is null, save without throwing")
    void testSaveWhenPreviousStateIsNull() throws StateDataInvalidDateException {
        // Given
        stateDataDao.setStateDataReturned(new StateData(null, 800001L, "welcomePage"));
        StateData stateDataUpdate = new StateData(StateDataType.INIT, 800000L, "5");
        // When
        stateDataService.saveStateData(interrogationId, stateDataUpdate, false, false);
        // Then
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data with verifyDate, when previous date is null, save without throwing")
    void testSaveWhenPreviousDateIsNullAndVerifyDate() throws StateDataInvalidDateException {
        // Given
        stateDataDao.setStateDataReturned(new StateData(StateDataType.INIT, null, "welcomePage"));
        StateData stateDataUpdate = new StateData(StateDataType.INIT, 800000L, "5");
        // When
        stateDataService.saveStateData(interrogationId, stateDataUpdate, true, false);
        // Then
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }
}
