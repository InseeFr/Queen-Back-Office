package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.infrastructure.dummy.InterrogationFakeDao;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.dummy.DataFakeService;
import fr.insee.queen.domain.interrogation.service.dummy.MetadataFakeService;
import fr.insee.queen.domain.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterrogationApiServiceTest {

    private InterrogationApiService interrogationApiService;
    private StateDataFakeService stateDataFakeService;
    private DataFakeService dataFakeService;
    private CampaignExistenceFakeService campaignExistenceFakeService;
    private MetadataFakeService metadataFakeService;
    private InterrogationFakeDao interrogationFakeDao;

    private static final String QUESTIONNAIRE_ID = "questionnaire-id";
    private static final String CAMPAIGN_ID = "campaign-id";

    @BeforeEach
    void init() {
        CacheManager cacheManager = new NoOpCacheManager();
        interrogationFakeDao = new InterrogationFakeDao();
        stateDataFakeService = new StateDataFakeService();
        campaignExistenceFakeService = new CampaignExistenceFakeService();
        dataFakeService = new DataFakeService();
        metadataFakeService = new MetadataFakeService();
        interrogationApiService = new InterrogationApiService(interrogationFakeDao, stateDataFakeService, dataFakeService,
                campaignExistenceFakeService, metadataFakeService, cacheManager);
    }

    @Test
    @DisplayName("On creating interrogation, check questionnaire is linked to campaign")
    void testCreate02() throws StateDataInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        interrogationFakeDao.setInterrogationExist(false);
        interrogationApiService.createInterrogation(interrogation);

        assertThat(campaignExistenceFakeService.isCheckCampaignLinkedToQuestionnaire()).isTrue();
    }

    @Test
    @DisplayName("On creating interrogation, when interrogation exists, throw exception")
    void testCreate03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        assertThatThrownBy(() -> interrogationApiService.createInterrogation(interrogation))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessage(String.format(InterrogationApiService.ALREADY_EXIST_MESSAGE, interrogation.id()));
    }

    @Test
    @DisplayName("On creating interrogation, when state data is null, don't save it")
    void testCreate04() throws StateDataInvalidDateException {
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        interrogationFakeDao.setInterrogationExist(false);
        interrogationApiService.createInterrogation(interrogation);
        assertThat(interrogationFakeDao.getInterrogationCreated()).isEqualTo(interrogation);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On creating interrogation, when state data is not null, save it")
    void testCreate05() throws StateDataInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        interrogationFakeDao.setInterrogationExist(false);
        interrogationApiService.createInterrogation(interrogation);
        assertThat(interrogationFakeDao.getInterrogationCreated()).isEqualTo(interrogation);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
    }

    @Test
    @DisplayName("On updating interrogation, when interrogation not exist, throw exception")
    void testUpdate01() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        interrogationFakeDao.setInterrogationExist(false);
        assertThatThrownBy(() -> interrogationApiService.updateInterrogation(interrogation))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format(InterrogationApiService.NOT_FOUND_MESSAGE, interrogation.id()));
        assertThat(interrogationFakeDao.getInterrogationUpdated()).isNull();
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On updating interrogation, when state data is null, don't save it")
    void testUpdate02() {
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        interrogationApiService.updateInterrogation(interrogation);
        assertThat(interrogationFakeDao.getInterrogationUpdated()).isEqualTo(interrogation);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On updating interrogation, when state data is not null, save it")
    void testUpdate03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        interrogationApiService.updateInterrogation(interrogation);
        assertThat(interrogationFakeDao.getInterrogationUpdated()).isEqualTo(interrogation);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
    }

    @Test
    @DisplayName("On updating interrogation, when date is invalid on state data, ignore the error")
    void testUpdate04() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        Interrogation interrogation = new Interrogation("11", "survey-unit-id-11", CAMPAIGN_ID, QUESTIONNAIRE_ID,
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                stateData);
        stateDataFakeService.setDateInvalid(true);
        interrogationApiService.updateInterrogation(interrogation);
        assertThat(interrogationFakeDao.getInterrogationUpdated()).isEqualTo(interrogation);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }
    @ParameterizedTest
    @MethodSource("nullOrEmpTyData")
    @DisplayName("On updating interrogation, when data is null or empty, don't update data")
    void testUpdateDataStateData02(ObjectNode data) {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        String interrogationId = "11";
        interrogationApiService.updateInterrogation(interrogationId, data, stateData);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
        assertThat(dataFakeService.getDataSaved()).isNull();
    }


    @Test
    @DisplayName("On updating interrogation, when state data is not null, save it")
    void testUpdateDataStateData03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("field1", 5);
        String interrogationId = "11";
        interrogationApiService.updateInterrogation(interrogationId, data, stateData);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
        assertThat(dataFakeService.getDataSaved()).isEqualTo(data);
    }

    @Test
    @DisplayName("On updating interrogation, when state data update throws an invalid date exception, check data is saved ")
    void testUpdateDataStateData04() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("field1", 5);
        String interrogationId = "11";
        stateDataFakeService.setDateInvalid(true);
        interrogationApiService.updateInterrogation(interrogationId, data, stateData);
        assertThat(dataFakeService.getDataSaved()).isEqualTo(data);
    }

    @Test
    @DisplayName("Retrieve interrogation metadata")
    void testRetrieveMetaData01() {
        String interrogationId = "11";

        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        metadataFakeService.setMetadata(metadata);

        ArrayNode personalization = JsonNodeFactory.instance.arrayNode();
        InterrogationPersonalization interrogationPersonalization = new InterrogationPersonalization(
                interrogationId, "questionnaire-id", personalization);
        interrogationFakeDao.setInterrogationPersonalization(interrogationPersonalization);

        InterrogationMetadata interrogationMetadata = interrogationApiService.getInterrogationMetadata(interrogationId);
        assertThat(interrogationMetadata.metadata()).isEqualTo(metadata);
        assertThat(interrogationMetadata.interrogationPersonalization()).isEqualTo(interrogationPersonalization);
    }

    static Stream<ObjectNode> nullOrEmpTyData() {
        return Stream.of(null, JsonNodeFactory.instance.objectNode());
    }
}