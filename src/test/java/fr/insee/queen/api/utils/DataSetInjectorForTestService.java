package fr.insee.queen.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.campaign.service.*;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.dataset.service.exception.DataSetException;
import fr.insee.queen.api.dataset.service.DataSetInjectorService;
import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.paradata.service.ParadataEventService;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
@Primary
public class DataSetInjectorForTestService implements DataSetInjectorService {
    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final CampaignService campaignService;
    private final SurveyUnitService surveyUnitService;
    private final ParadataEventService paradataEventService;
    private final QuestionnaireModelService questionnaireModelService;
    private final NomenclatureService nomenclatureService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CURRENT_PAGE = "2.3#5";

    private static final String SEPARATOR = "----------------------";

    private static final String BASE_RESOURCE_DIRECTORY = "db/dataset/test/";

    @Transactional
    public void createDataSet() {
        log.info("Dataset for tests - creation start");
        createSimpsonsDataSet();
        log.info(SEPARATOR);
        createVqsDataSet();
        log.info(SEPARATOR);
        createStromaeLogementDataSet();
        log.info(SEPARATOR);
        createQueenLogementDataSet();
        log.info("Dataset for tests - creation end");
    }

    public void createQueenLogementDataSet() {
        log.info("Queen Logement dataset - start");

        ObjectNode jsonQuestionnaireModelQueenLog;

        try {
            jsonQuestionnaireModelQueenLog = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/logS1Tel.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error getting json questionnaire/metadatas", e);
            throw new DataSetException(e.getMessage());
        }

        List<String> nomenclatureIds = createLogementNomenclature();
        String campaignId = "LOG2021X11Tel";
        String questionnaireId = "LOG2021X11Tel";

        createQuestionnaire(questionnaireId, "Enquête Logement 2022 - Séquence 1 - HR", jsonQuestionnaireModelQueenLog, nomenclatureIds);
        createCampaign(campaignId, "Enquête Logement 2022 - Séquence 1 - HR", objectMapper.createObjectNode(), Set.of(questionnaireId));
        createSurveyUnitWithParadata(String.format("%s_01", campaignId), campaignId, questionnaireId);
        createSurveyUnitWithParadata(String.format("%s_02", campaignId), campaignId, questionnaireId);
        createSurveyUnitWithParadata(String.format("%s_03", campaignId), campaignId, questionnaireId);

        log.info("Queen Logement Dataset - end");
    }

    public void createStromaeLogementDataSet() {
        log.info("Stromae Logement dataset - start");

        ObjectNode jsonQuestionnaireModelStromaeLog;
        ObjectNode jsonMetadata;

        try {
            jsonQuestionnaireModelStromaeLog = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/logS1Web.json"), ObjectNode.class);
            jsonMetadata = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/metadata/metadata.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error getting json questionnaire/metadatas", e);
            throw new DataSetException(e.getMessage());
        }

        List<String> nomenclatureIds = createLogementNomenclature();
        String campaignId = "LOG2021X11Web";
        String questionnaireId = "LOG2021X11Web";

        createQuestionnaire(questionnaireId, "Enquête Logement 2022 - Séquence 1 - HR - Web", jsonQuestionnaireModelStromaeLog, nomenclatureIds);
        createCampaign(campaignId, "Enquête Logement 2022 - Séquence 1 - HR - Web", jsonMetadata, Set.of(questionnaireId));
        createSurveyUnitWithParadata(String.format("%s-01", campaignId), campaignId, questionnaireId);
        createSurveyUnitWithParadata(String.format("%s-02", campaignId), campaignId, questionnaireId);
        createSurveyUnitWithParadata(String.format("%s-03", campaignId), campaignId, questionnaireId);

        log.info("Stromae Logement Dataset - end");
    }

    private List<String> createLogementNomenclature() {
        ArrayNode jsonArrayNomenclatureDepNais;
        ArrayNode jsonArrayNomenclatureNationEtr;
        ArrayNode jsonArrayNomenclaturePaysNais;
        ArrayNode jsonArrayNomenclatureCogCom;
        String depNaisId = "L_DEPNAIS";
        String nationEtrId = "L_NATIONETR";
        String paysNaisId = "L_PAYSNAIS";
        String cogComId = "cog-communes";

        try {
            jsonArrayNomenclatureDepNais = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/nomenclatures/L_DEPNAIS.json"), ArrayNode.class);
            jsonArrayNomenclatureNationEtr = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/nomenclatures/L_NATIONETR.json"), ArrayNode.class);
            jsonArrayNomenclaturePaysNais = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/nomenclatures/L_PAYSNAIS.json"), ArrayNode.class);
            jsonArrayNomenclatureCogCom = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "logement/nomenclatures/cog-communes.json"), ArrayNode.class);
        } catch (IOException e) {
            log.error("Error getting json nomenclatures", e);
            throw new DataSetException(e.getMessage());
        }

        createNomenclature(depNaisId, "départements français", jsonArrayNomenclatureDepNais);
        createNomenclature(nationEtrId, "nationalités", jsonArrayNomenclatureNationEtr);
        createNomenclature(paysNaisId, "pays", jsonArrayNomenclaturePaysNais);
        createNomenclature(cogComId, "communes françaises", jsonArrayNomenclatureCogCom);

        return List.of(depNaisId, nationEtrId, paysNaisId, cogComId);
    }

    private void createVqsDataSet() {
        log.info("VQS dataset - start");
        ArrayNode jsonArrayNomenclatureCities2019;
        ArrayNode jsonArrayRegions2019;
        ObjectNode jsonQuestionnaireModelVqs;
        try {
            jsonArrayNomenclatureCities2019 = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "nomenclature/public_communes-2019.json"), ArrayNode.class);
            jsonArrayRegions2019 = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "nomenclature/public_regions-2019.json"), ArrayNode.class);
            jsonQuestionnaireModelVqs = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "vqs.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error getting json for VQS Dataset", e);
            throw new DataSetException(e.getMessage());
        }

        String campaignId = "VQS2021X00";
        String questionnaireId = "VQS2021X00";
        String nomenclatureId1 = "cities2019";
        String nomenclatureId2 = "regions2019";

        createNomenclature(nomenclatureId1, "french cities 2019", jsonArrayNomenclatureCities2019);
        createNomenclature(nomenclatureId2, "french regions 2019", jsonArrayRegions2019);

        createQuestionnaire(questionnaireId, "Questionnaire of the Everyday life and health survey 2021", jsonQuestionnaireModelVqs, List.of(nomenclatureId1, nomenclatureId2));

        createCampaign(campaignId, "Everyday life and health survey 2021", objectMapper.createObjectNode(), Set.of(questionnaireId));
        List<String> surveyUnitIds = List.of("20", "21", "22", "23");
        for (String surveyUnitId : surveyUnitIds) {
            createSurveyUnitWithParadata(surveyUnitId, campaignId, questionnaireId);
        }
        log.info("VQS dataset - end");
    }

    private void createSimpsonsDataSet() {
        log.info("Simpsons dataset - start");
        ArrayNode jsonArrayNomenclatureCities2019;
        ArrayNode jsonArrayRegions2019;
        ObjectNode jsonQuestionnaireModelSimpsons;
        try {
            jsonArrayNomenclatureCities2019 = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "nomenclature/public_communes-2019.json"), ArrayNode.class);
            jsonArrayRegions2019 = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "nomenclature/public_regions-2019.json"), ArrayNode.class);
            jsonQuestionnaireModelSimpsons = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "simpsons.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error getting json for Simpsons Dataset", e);
            throw new DataSetException(e.getMessage());
        }

        String campaignId = "SIMPSONS2020X00";
        String questionnaireId1 = "simpsons";
        String questionnaireId2 = "simpsonsV2";
        String nomenclatureId1 = "cities2019";
        String nomenclatureId2 = "regions2019";

        createNomenclature(nomenclatureId1, "french cities 2019", jsonArrayNomenclatureCities2019);
        createNomenclature(nomenclatureId2, "french regions 2019", jsonArrayRegions2019);

        createQuestionnaire("QmWithoutCamp", "Questionnaire with no campaign", jsonQuestionnaireModelSimpsons, List.of(nomenclatureId1));
        createQuestionnaire(questionnaireId1, "Questionnaire about the Simpsons tv show", jsonQuestionnaireModelSimpsons, List.of(nomenclatureId1, nomenclatureId2));
        createQuestionnaire(questionnaireId2, "Questionnaire about the Simpsons tv show version 2", jsonQuestionnaireModelSimpsons, List.of(nomenclatureId2));

        createCampaign(campaignId, "Survey on the Simpsons tv show 2020", objectMapper.createObjectNode(), Set.of(questionnaireId1, questionnaireId2));

        String surveyUnitId = "11";
        createSurveyUnit(surveyUnitId, campaignId, questionnaireId1,
                getPersonalizationValue(),
                getDataValue(),
                getCommentValue(),
                StateDataType.EXTRACTED);

        surveyUnitId = "12";
        createSurveyUnit(surveyUnitId, campaignId, questionnaireId1,
                objectMapper.createArrayNode(),
                objectMapper.createObjectNode(),
                objectMapper.createObjectNode(),
                StateDataType.INIT);

        surveyUnitId = "13";
        createSurveyUnit(surveyUnitId, campaignId, questionnaireId2,
                objectMapper.createArrayNode(),
                getDataValue(),
                objectMapper.createObjectNode(),
                StateDataType.INIT);

        surveyUnitId = "14";
        createSurveyUnit(surveyUnitId, campaignId, questionnaireId2,
                objectMapper.createArrayNode(),
                getDataValue(),
                objectMapper.createObjectNode(),
                StateDataType.INIT);
        log.info("Simpsons dataset - end");
    }

    private void createCampaign(String id, String label, ObjectNode jsonMetadata, Set<String> questionnaireIds) {
        if (campaignExistenceService.existsById(id)) {
            return;
        }

        log.info("Create Campaign {}", id);
        Campaign campaign = new Campaign(id, label, questionnaireIds, jsonMetadata.toString());
        campaignService.createCampaign(campaign);
    }

    private void createQuestionnaire(String id, String label, ObjectNode jsonQm, List<String> nomenclatureIds) {
        if (questionnaireModelExistenceService.existsById(id)) {
            return;
        }
        log.info("Create Questionnaire {}", id);
        QuestionnaireModel qm = QuestionnaireModel.createQuestionnaireWithoutCampaign(id, label, jsonQm.toString(), new HashSet<>(nomenclatureIds));
        questionnaireModelService.createQuestionnaire(qm);
    }

    private void createNomenclature(String id, String label, ArrayNode jsonNomenclature) {
        if (nomenclatureService.existsById(id)) {
            return;
        }
        log.info("Create nomenclature {}", id);
        Nomenclature nomenclature = new Nomenclature(id, label, jsonNomenclature.toString());
        nomenclatureService.saveNomenclature(nomenclature);
    }

    private void createSurveyUnitWithParadata(String surveyUnitId, String campaignId, String questionnaireModelId) {
        createSurveyUnit(surveyUnitId,
                campaignId,
                questionnaireModelId,
                objectMapper.createArrayNode(),
                objectMapper.createObjectNode(),
                objectMapper.createObjectNode(),
                new StateData(StateDataType.INIT, 900000000L, "1"));
        createParadataEvents(surveyUnitId);
    }

    private void createSurveyUnit(String surveyUnitId, String campaignId, String questionnaireModelId,
                                  ArrayNode personalization, ObjectNode data, ObjectNode comment, StateDataType state) {
        StateData stateData = null;
        if (state != null) {
            stateData = new StateData(state, 1111111111L, CURRENT_PAGE);
        }
        createSurveyUnit(surveyUnitId, campaignId, questionnaireModelId, personalization, data, comment, stateData);
    }

    private void createSurveyUnit(String surveyUnitId, String campaignId, String questionnaireModelId,
                                  ArrayNode personalization, ObjectNode data, ObjectNode comment, StateData stateData) {
        if (surveyUnitService.existsById(surveyUnitId)) {
            return;
        }
        log.info("Create survey unit {}", surveyUnitId);
        SurveyUnit surveyunit = new SurveyUnit(surveyUnitId,
                campaignId,
                questionnaireModelId,
                personalization.toString(),
                data.toString(),
                comment.toString(),
                stateData);
        surveyUnitService.createSurveyUnit(surveyunit);
    }

    private void createParadataEvents(String surveyUnitId) {
        log.info("Create paradata for survey unit {}", surveyUnitId);
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("idSU", JsonNodeFactory.instance.textNode(surveyUnitId));
        paradataEventService.createParadataEvent(surveyUnitId, rootNode.toString());
        paradataEventService.createParadataEvent(surveyUnitId, rootNode.toString());
    }

    private ArrayNode getPersonalizationValue() {
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "personalization.json"), ArrayNode.class);
        } catch (Exception e) {
            log.error("Error getting personalization value", e);
            throw new DataSetException(e.getMessage());
        }
    }

    private ObjectNode getDataValue() {
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "data.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error getting json data value", e);
            throw new DataSetException(e.getMessage());
        }
    }

    private ObjectNode getCommentValue() {
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(BASE_RESOURCE_DIRECTORY + "comment.json"), ObjectNode.class);
        } catch (Exception e) {
            log.error("Error retrieving comment value", e);
            throw new DataSetException(e.getMessage());
        }
    }

}
