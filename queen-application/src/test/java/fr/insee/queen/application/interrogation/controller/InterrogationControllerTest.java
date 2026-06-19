package fr.insee.queen.application.interrogation.controller;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.interrogation.dto.output.InterrogationBySurveyUnitDto;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.controller.dummy.MetadataFakeConverter;
import fr.insee.queen.application.interrogation.dto.input.*;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterrogationControllerTest {
    private InterrogationController interrogationController;
    private PilotageFakeComponent pilotageComponent;
    private InterrogationFakeService interrogationFakeService;
    private MetadataFakeConverter metadataConverter;

    @BeforeEach
    void init() {
        metadataConverter = new MetadataFakeConverter();
        pilotageComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        interrogationController = new InterrogationController(interrogationFakeService, pilotageComponent, metadataConverter);
    }

    @Test
    @DisplayName("Should update interrogation and transform state-data to null if state from input state data is null")
    void testUpdateInterrogation01() {
        // given
        StateDataForInterrogationUpdateInput stateData = new StateDataForInterrogationUpdateInput(null, 123456789L, "2.3");
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, stateData);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should update interrogation and transform state-data to null if input state data is null")
    void testUpdateInterrogation02() {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should return interrogation with data")
    void testGetInterrogation01() {
        // given
        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION1_ID);

        // then
        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isEqualTo(interrogation.data());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData().state()).isEqualTo(interrogation.stateData().state());
        assertThat(interrogationDto.stateData().currentPage()).isEqualTo(interrogation.stateData().currentPage());
        assertThat(interrogationDto.stateData().date()).isEqualTo(interrogation.stateData().date());
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @Test
    @DisplayName("Should return interrogations by survey-unit")
    void testGetInterrogationsBySurveyUnitId() {
        // when
        List<InterrogationBySurveyUnitDto> interrogations = interrogationController.getInterrogationsBySurveyUnit("survey-unit-id1");

        // then
        assertThat(interrogations).isNotNull()
                .hasSize(1);
        InterrogationBySurveyUnitDto interrogation = interrogations.getFirst();
        assertThat(interrogation).isNotNull();
        assertThat(interrogation.interrogationId()).isEqualTo(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogation.campaignId()).isEqualTo("campaign-id");
    }

    @Test
    @DisplayName("Should return empty list  if survey-unit unknown")
    void testGetInterrogationsBySurveyUnitId2() {
        // when
        List<InterrogationBySurveyUnitDto> interrogations = interrogationController.getInterrogationsBySurveyUnit("survey-unit-unknown");

        // then
        assertThat(interrogations).isNotNull()
                .isEmpty();
    }
}
