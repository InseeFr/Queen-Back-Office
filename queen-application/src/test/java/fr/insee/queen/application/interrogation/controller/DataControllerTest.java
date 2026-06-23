package fr.insee.queen.application.interrogation.controller;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.service.dummy.DataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataControllerTest {

    private DataController dataController;
    private DataFakeService dataFakeService;

    @BeforeEach
    void init() {
        PilotageFakeComponent pilotageFakeComponent = new PilotageFakeComponent();
        dataFakeService = new DataFakeService();
        dataController = new DataController(dataFakeService, pilotageFakeComponent);
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
        assertThat(dataFakeService.getCleanedGroupId()).isEqualTo("SIMPSONS2020X00");
        assertThat(dataFakeService.getCleanedInterrogationIds()).containsExactlyElementsOf(ids);
    }
}
