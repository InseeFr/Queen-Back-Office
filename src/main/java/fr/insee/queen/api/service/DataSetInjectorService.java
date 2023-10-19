package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.domain.*;
import fr.insee.queen.api.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class DataSetInjectorService {
    private final CampaignRepository campaignRepository;
    private final SurveyUnitRepository surveyUnitRepository;
    private final ParadataEventRepository paradataEventRepository;
    private final MetadataRepository metadataRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final NomenclatureRepository nomenclatureRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CURRENT_PAGE = "2.3#5";

    @Transactional
    public void createDataSet() {
        log.info("Dataset creation start");
        createSimpsonVqsDataSet();
        createLogementDataSet();
        log.info("Dataset creation end");
    }

    public void createLogementDataSet() {
        log.info("Dataset Queen Logement creation start");

        JsonNode jsonArrayQuestionnaireModelQueenLog = objectMapper.createObjectNode();
        JsonNode jsonArrayQuestionnaireModelStromaeLog = objectMapper.createObjectNode();
        JsonNode jsonArrayMetadata = objectMapper.createObjectNode();


        try {
            jsonArrayQuestionnaireModelQueenLog = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/logS1Tel.json"));
            jsonArrayQuestionnaireModelStromaeLog = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/logS1Web.json"));
            jsonArrayMetadata = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/metadata/metadata.json"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Dataset Logement : creation Nomenclature");
        ArrayList<Nomenclature> listNomenclature = createLogementNomenclature();

        log.info("Dataset Logement : creation Campaign Stromae");
        injectCampaign("LOG2021X11Web", "Enquête Logement 2022 - Séquence 1 - HR - Web", jsonArrayQuestionnaireModelStromaeLog, listNomenclature, jsonArrayMetadata);

        log.info("Dataset Logement : creation Campaign Queen");
        injectCampaign("LOG2021X11Tel", "Enquête Logement 2022 - Séquence 1 - HR", jsonArrayQuestionnaireModelQueenLog, listNomenclature, null);

        log.info("Dataset Logement : end Creation");
    }

    private void injectCampaign(String id, String label, JsonNode jsonQm, ArrayList<Nomenclature> listNomenclature, JsonNode jsonMetadata) {
        Pair<Campaign, QuestionnaireModel> campQm = createCampaign(id, label, jsonQm, jsonMetadata, listNomenclature);
        Campaign camp = campQm.getFirst();
        QuestionnaireModel qm = campQm.getSecond();
        log.info("Dataset : creation SurveyUnit");

        initSurveyUnit(String.format("%s_01", id), camp, qm);
        initSurveyUnit(String.format("%s_02", id), camp, qm);
        initSurveyUnit(String.format("%s_03", id), camp, qm);

    }

    private ArrayList<Nomenclature> createLogementNomenclature() {
        JsonNode jsonArrayNomenclatureDepNais = objectMapper.createObjectNode();
        JsonNode jsonArrayNomenclatureNationEtr = objectMapper.createObjectNode();
        JsonNode jsonArrayNomenclaturePaysNais = objectMapper.createObjectNode();
        JsonNode jsonArrayNomenclatureCogCom = objectMapper.createObjectNode();

        try {
            jsonArrayNomenclatureDepNais = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/nomenclatures/L_DEPNAIS.json"));
            jsonArrayNomenclatureNationEtr = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/nomenclatures/L_NATIONETR.json"));
            jsonArrayNomenclaturePaysNais = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/nomenclatures/L_PAYSNAIS.json"));
            jsonArrayNomenclatureCogCom = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/logement/nomenclatures/cog-communes.json"));
        } catch (IOException e) {
            log.error("Error getting json nomenclatures");
            e.printStackTrace();
        }

        Nomenclature nomenclatureDepNais = new Nomenclature("L_DEPNAIS", "départements français", jsonArrayNomenclatureDepNais.toString());
        Nomenclature nomenclatureNationEtr = new Nomenclature("L_NATIONETR", "nationalités", jsonArrayNomenclatureNationEtr.toString());
        Nomenclature nomenclaturePaysNais = new Nomenclature("L_PAYSNAIS", "pays", jsonArrayNomenclaturePaysNais.toString());
        Nomenclature nomenclatureCogCom = new Nomenclature("cog-communes", "communes françaises", jsonArrayNomenclatureCogCom.toString());

        ArrayList<Nomenclature> listNomenclature = new ArrayList<>(Arrays.asList(nomenclatureDepNais, nomenclatureNationEtr, nomenclaturePaysNais, nomenclatureCogCom));
        createNomenclatures(listNomenclature);
        return listNomenclature;
    }

    private Pair<Campaign, QuestionnaireModel> createCampaign(String id, String label, JsonNode jsonQm, JsonNode jsonMetadata, ArrayList<Nomenclature> listNomenclature) {
        log.info(String.format("Create Campaing %s", id));
        Campaign camp = new Campaign(id, label, null);
        QuestionnaireModel qm = new QuestionnaireModel(id, label, jsonQm.toString(), new HashSet<>(listNomenclature), camp);
        if (campaignRepository.findById(camp.id()).isEmpty()) {
            camp.questionnaireModels(new HashSet<>(List.of(qm)));
            campaignRepository.save(camp);
            if (jsonMetadata != null) {
                Metadata metadata = new Metadata(UUID.randomUUID(), jsonMetadata.toString(), camp);
                metadataRepository.save(metadata);
            }
            if (questionnaireModelRepository.findById(qm.id()).isEmpty()) {
                questionnaireModelRepository.save(qm);
            }
        }
        return Pair.of(camp, qm);
    }

    private void createNomenclatures(ArrayList<Nomenclature> listNomenclature) {
        log.info("Creation Nomenclatures");
        listNomenclature.forEach(nomenclature -> {
                    if (nomenclatureRepository.findById(nomenclature.id()).isEmpty()) {
                        nomenclatureRepository.save(nomenclature);
                    }
                }
        );
    }

    private void initSurveyUnit(String id, Campaign campaign, QuestionnaireModel questionnaireModel) {
        if (surveyUnitRepository.findById(id).isEmpty()) {
            log.info("initSurveyUnit -> SU Do not present, we create it");
            createSurveyUnit(id, campaign, questionnaireModel);
            log.info("End of SU Creation");
        }

    }

    private void createSimpsonVqsDataSet() {
        JsonNode jsonArrayNomenclatureCities2019 = objectMapper.createObjectNode();
        JsonNode jsonArrayRegions2019 = objectMapper.createObjectNode();
        JsonNode jsonQuestionnaireModelSimpsons = objectMapper.createObjectNode();
        JsonNode jsonQuestionnaireModelVqs = objectMapper.createObjectNode();
        try {
            jsonArrayNomenclatureCities2019 = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/nomenclature/public_communes-2019.json"));
            jsonArrayRegions2019 = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/nomenclature/public_regions-2019.json"));
            jsonQuestionnaireModelSimpsons = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/simpsons.json"));
            jsonQuestionnaireModelVqs = objectMapper.readTree(getClass().getClassLoader().getResourceAsStream("db/dataset/vqs.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Nomenclature n = createNomenclature("cities2019", "french cities 2019", jsonArrayNomenclatureCities2019);
        Nomenclature n2 = createNomenclature("regions2019", "french regions 2019", jsonArrayRegions2019);

        QuestionnaireModel qmWithoutCamp = new QuestionnaireModel("QmWithoutCamp", "Questionnaire with no campaign", jsonQuestionnaireModelSimpsons.toString(), new HashSet<>(List.of(n)), null);
        if (questionnaireModelRepository.findById(qmWithoutCamp.id()).isEmpty()) {
            questionnaireModelRepository.save(qmWithoutCamp);
        }


        Campaign camp = new Campaign("SIMPSONS2020X00", "Survey on the Simpsons tv show 2020", null);
        QuestionnaireModel qm = new QuestionnaireModel("simpsons", "Questionnaire about the Simpsons tv show", jsonQuestionnaireModelSimpsons.toString(), new HashSet<>(List.of(n)), camp);
        camp.questionnaireModels(new HashSet<>(List.of(qm)));
        QuestionnaireModel qmSimpsons2 = new QuestionnaireModel("simpsonsV2", "Questionnaire about the Simpsons tv show version 2", jsonQuestionnaireModelSimpsons.toString(), new HashSet<>(List.of(n)), camp);
        camp.questionnaireModels(new HashSet<>(List.of(qm)));
        createCampaign1(camp, qm, qmSimpsons2);


        Campaign camp2 = new Campaign("VQS2021X00", "Everyday life and health survey 2021", null);
        QuestionnaireModel qm2 = new QuestionnaireModel("VQS2021X00", "Questionnaire of the Everyday life and health survey 2021", jsonQuestionnaireModelVqs.toString(), new HashSet<>(List.of(n, n2)), camp2);
        createCampaign2(camp2, qm2);
    }

    private void createCampaign2(Campaign camp2, QuestionnaireModel qm2) {
        if (campaignRepository.findById(camp2.id()).isEmpty()) {
            campaignRepository.save(camp2);
            if (questionnaireModelRepository.findById(qm2.id()).isEmpty()) {
                questionnaireModelRepository.save(qm2);
            }
            camp2.questionnaireModels(new HashSet<>(List.of(qm2)));
            campaignRepository.save(camp2);
            Metadata md2 = new Metadata(UUID.randomUUID(), objectMapper.createObjectNode().toString(), camp2);
            metadataRepository.save(md2);

            List<String> surveyUnitIds = List.of("20", "21", "22", "23");
            for (String surveyUnitId : surveyUnitIds) {
                if (surveyUnitRepository.findById(surveyUnitId).isEmpty()) {
                    createSurveyUnitCampaign2(surveyUnitId, camp2, qm2);
                }
            }
        }
    }

    private void createCampaign1(Campaign camp, QuestionnaireModel qm, QuestionnaireModel qm2) {
        if (campaignRepository.findById(camp.id()).isEmpty()) {
            campaignRepository.save(camp);
            if (questionnaireModelRepository.findById(qm.id()).isEmpty()) {
                questionnaireModelRepository.save(qm);
            }
            if (questionnaireModelRepository.findById(qm2.id()).isEmpty()) {
                questionnaireModelRepository.save(qm2);
            }
            Set<QuestionnaireModel> models = camp.questionnaireModels();
            models.add(qm);
            models.add(qm2);
            campaignRepository.save(camp);

            Metadata md = new Metadata(UUID.randomUUID(), objectMapper.createObjectNode().toString(), camp);
            metadataRepository.save(md);

            String surveyUnitId = "11";
            createSurveyUnitCampaign1(surveyUnitId, camp, qm, getPersonalizationValue(), getDataValue(), getCommentValue(), StateDataType.EXTRACTED);

            surveyUnitId = "12";
            createSurveyUnitCampaign1(surveyUnitId, camp, qm,
                    objectMapper.createArrayNode().toString(),
                    objectMapper.createObjectNode().toString(),
                    objectMapper.createObjectNode().toString(),
                    StateDataType.INIT);

            surveyUnitId = "13";
            createSurveyUnitCampaign1(surveyUnitId, camp, qm2,
                    objectMapper.createArrayNode().toString(),
                    getDataValue(),
                    objectMapper.createObjectNode().toString(),
                    StateDataType.INIT);

            surveyUnitId = "14";
            createSurveyUnitCampaign1(surveyUnitId, camp, qm2,
                    objectMapper.createArrayNode().toString(),
                    getDataValue(),
                    objectMapper.createObjectNode().toString(),
                    StateDataType.INIT);
        }
    }

    private StateData createStateData(StateDataType state, long date, String currentPage) {
        return new StateData(state, date, currentPage);
    }

    private void createParadataEvents(SurveyUnit su) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("idSU", JsonNodeFactory.instance.textNode(su.id()));
        ParadataEvent pde = new ParadataEvent(UUID.randomUUID(), rootNode.toString(), su);
        ParadataEvent pde2 = new ParadataEvent(UUID.randomUUID(), rootNode.toString(), su);
        paradataEventRepository.save(pde);
        paradataEventRepository.save(pde2);
    }

    private Nomenclature createNomenclature(String id, String label, JsonNode jsonNomenclature) {
        Nomenclature n = new Nomenclature(id, label, jsonNomenclature.toString());
        if (nomenclatureRepository.findById(n.id()).isEmpty()) {
            nomenclatureRepository.save(n);
        }
        return n;
    }

    private String getCommentValue() {
        String comment = "{}";
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("db/dataset/comment.json"), ObjectNode.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    private SurveyUnit createSurveyUnit(String surveyUnitId, Campaign camp, QuestionnaireModel qm) {
        StateData stateData = createStateData(StateDataType.INIT, 900000000L, "1");
        SurveyUnit su = new SurveyUnit(surveyUnitId, camp, qm, stateData,
                objectMapper.createArrayNode().toString(),
                objectMapper.createObjectNode().toString(),
                objectMapper.createObjectNode().toString());
        surveyUnitRepository.save(su);
        createParadataEvents(su);
        return su;
    }

    private void createSurveyUnitCampaign2(String surveyUnitId, Campaign camp, QuestionnaireModel qm) {
        SurveyUnit su = createSurveyUnit(surveyUnitId, camp, qm);
        createParadataEvents(su);
    }

    private void createSurveyUnitCampaign1(String surveyUnitId, Campaign camp, QuestionnaireModel qm,
                                           String personalization, String data, String comment, StateDataType state) {
        SurveyUnit su = new SurveyUnit(surveyUnitId, camp, qm, null, personalization, data, comment);
        if(state != null) {
            StateData stateData = createStateData(state, 1111111111L, CURRENT_PAGE);
            su.stateData(stateData);
        }
        surveyUnitRepository.save(su);
    }

    private String getPersonalizationValue() {
        String personalization = "[]";
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("db/dataset/personalization.json"), ArrayNode.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return personalization;
    }

    private String getDataValue() {
        String data = "{}";
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("db/dataset/data.json"), ObjectNode.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
