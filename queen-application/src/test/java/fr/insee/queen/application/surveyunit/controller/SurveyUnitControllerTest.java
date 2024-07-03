package fr.insee.queen.application.surveyunit.controller;

import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.controller.dummy.MetadataFakeConverter;
import fr.insee.queen.application.surveyunit.dto.input.StateDataForSurveyUnitUpdateInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitUpdateInput;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitControllerTest {
    private SurveyUnitController surveyUnitController;
    private PilotageFakeComponent pilotageComponent;
    private SurveyUnitFakeService surveyUnitFakeService;
    private MetadataFakeConverter metadataConverter;

    @BeforeEach
    public void init() {
        metadataConverter = new MetadataFakeConverter();
        pilotageComponent = new PilotageFakeComponent();
        surveyUnitFakeService = new SurveyUnitFakeService();
        surveyUnitController = new SurveyUnitController(surveyUnitFakeService, pilotageComponent, metadataConverter);
    }

    @Test
    @DisplayName("On update survey-unit, should transform state-data to null if state from input state data is null")
    void testUpdateSurveyUnit01() {
        StateDataForSurveyUnitUpdateInput stateData = new StateDataForSurveyUnitUpdateInput(null, 123456789L, "2.3");
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, stateData);
        surveyUnitController.updateSurveyUnitById("11", suInput);
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("On update survey-unit, should transform state-data to null if input state data is null")
    void testUpdateSurveyUnit02() {
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        surveyUnitController.updateSurveyUnitById("11", suInput);
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }
}
