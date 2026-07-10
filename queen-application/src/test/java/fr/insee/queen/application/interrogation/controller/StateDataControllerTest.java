package fr.insee.queen.application.interrogation.controller;

import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.dto.input.StateDataInput;
import fr.insee.queen.application.interrogation.dto.input.StateDataTypeInput;
import fr.insee.queen.application.interrogation.dto.output.InterrogationOkNokDto;
import fr.insee.queen.application.interrogation.dto.output.StateDataDto;
import fr.insee.queen.application.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StateDataControllerTest {

    private StateDataController stateDataController;
    private PilotageFakeComponent pilotageFakeComponent;
    private InterrogationFakeService interrogationFakeService;
    private StateDataFakeService stateDataFakeService;

    @BeforeEach
    void init() {
        pilotageFakeComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        stateDataFakeService = new StateDataFakeService();
        stateDataController = new StateDataController(stateDataFakeService, interrogationFakeService, pilotageFakeComponent);
    }

    @Test
    @DisplayName("Should return state-data for interrogation and check habilitations")
    void testGetStateData01() {
        // when
        StateDataDto stateData = stateDataController.getStateDataByInterrogation(InterrogationFakeService.INTERROGATION1_ID);

        // then
        StateData expected = stateDataFakeService.getStateData(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(stateData.state()).isEqualTo(expected.state());
        assertThat(stateData.date()).isEqualTo(expected.date());
        assertThat(stateData.currentPage()).isEqualTo(expected.currentPage());
    }

    @Test
    @DisplayName("Should update state-data and check habilitations")
    void testUpdateStateData01() throws StateDataInvalidDateException {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.COMPLETED, "5.0");

        // when
        stateDataController.setStateData(InterrogationFakeService.INTERROGATION1_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @Test
    @DisplayName("Should split known and unknown interrogation ids into OK and NOK lists")
    void testGetStateDataByInterrogations01() {
        // given
        String unknownId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa99";
        List<String> ids = List.of(
                InterrogationFakeService.INTERROGATION1_ID,
                InterrogationFakeService.INTERROGATION2_ID,
                unknownId);

        // when
        InterrogationOkNokDto result = stateDataController.getStateDataByInterrogations(ids);

        // then
        assertThat(result.interrogationOK())
                .extracting("id")
                .containsExactlyInAnyOrder(
                        InterrogationFakeService.INTERROGATION1_ID,
                        InterrogationFakeService.INTERROGATION2_ID);
        assertThat(result.interrogationOK())
                .extracting(dto -> dto.stateData().state())
                .containsExactlyInAnyOrder(StateDataType.INIT, StateDataType.VALIDATED);
        assertThat(result.interrogationNOK())
                .extracting("id")
                .containsExactly(unknownId);
    }

    @Test
    @DisplayName("Should return only NOK entries when no interrogation is found")
    void testGetStateDataByInterrogations02() {
        // given
        List<String> ids = List.of("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa98", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa99");

        // when
        InterrogationOkNokDto result = stateDataController.getStateDataByInterrogations(ids);

        // then
        assertThat(result.interrogationOK()).isEmpty();
        assertThat(result.interrogationNOK())
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(ids);
    }
}
