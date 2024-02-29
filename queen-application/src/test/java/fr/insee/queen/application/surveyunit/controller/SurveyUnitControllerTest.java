package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.dto.input.StateDataInput;
import fr.insee.queen.application.surveyunit.dto.input.StateDataTypeInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitDataStateDataUpdateInput;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitControllerTest {
    private PilotageFakeComponent pilotageFakeComponent;
    private SurveyUnitController surveyUnitController;
    private SurveyUnitFakeService surveyUnitFakeService;

    @BeforeEach
    void init() {
        surveyUnitFakeService = new SurveyUnitFakeService();
        pilotageFakeComponent = new PilotageFakeComponent();
        surveyUnitController = new SurveyUnitController(surveyUnitFakeService, pilotageFakeComponent);
    }

    @Test
    @DisplayName("when updating data/state data, verify habilitation")
    void testSurveyUnitUpdateDataStateData01() {
        String surveyUnitId = "11";
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        StateDataInput stateData = new StateDataInput(StateDataTypeInput.INIT, 0L, "2.3");
        SurveyUnitDataStateDataUpdateInput su = new SurveyUnitDataStateDataUpdateInput(data, stateData);
        surveyUnitController.updateSurveyUnitDataStateDataById(surveyUnitId, su);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(surveyUnitFakeService.isCheckSurveyUnitUpdate()).isTrue();
    }
}
