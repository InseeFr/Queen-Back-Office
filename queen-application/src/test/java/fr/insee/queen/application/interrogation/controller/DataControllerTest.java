package fr.insee.queen.application.interrogation.controller;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.service.dummy.DataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataControllerTest {

    private DataController dataController;
    private PilotageFakeComponent pilotageFakeComponent;
    private InterrogationFakeService interrogationFakeService;
    private StateDataFakeService stateDataFakeService;
    private DataFakeService dataFakeService;
    private AuthenticationFakeHelper authenticationFakeHelper;

    @BeforeEach
    void init() {
        pilotageFakeComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        stateDataFakeService = new StateDataFakeService();
        dataFakeService = new DataFakeService();
        authenticationFakeHelper = new AuthenticationFakeHelper();
        dataController = new DataController(dataFakeService, pilotageFakeComponent, stateDataFakeService, interrogationFakeService, authenticationFakeHelper);
    }

    @Test
    @DisplayName("Should return interrogation data")
    void testGetInterrogation01() {
        // given / when
        ObjectNode data = dataController.getDataByInterrogation(InterrogationFakeService.INTERROGATION1_ID);

        // then
        ObjectNode interrogationData = dataFakeService.getData(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(data).isEqualTo(interrogationData);
    }

    @Test
    @DisplayName("Should delegate cleaning of extracted data by ids to data service")
    void cleanExtractedDataByIds_delegates() {
        // given
        List<String> ids = List.of("11", "12", "13");

        // when
        dataController.cleanExtractedDataByIds("SIMPSONS2020X00", ids);

        // then
        assertThat(dataFakeService.getCleanedCampaignId()).isEqualTo("SIMPSONS2020X00");
        assertThat(dataFakeService.getCleanedInterrogationIds()).containsExactlyElementsOf(ids);
    }
}
