package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class DataSetInjectorService {
	private final CampaignService campaignservice;
	private final SurveyUnitService surveyUnitService;
	private final DataService dataService;
	private final CommentService commentService;
	private final ParadataEventService paradataEventService;
	private final MetadataService metadataService;
	private final PersonalizationService personalizationService;
	private final StateDataService stateDataService;
	private final QuestionnaireModelService questionnaireModelService;
	private final NomenclatureService nomenclatureService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String CURRENT_PAGE = "2.3#5";
	private static final String FORCED_PROPERTY = "FORCED";
	private static final String EDITED_PROPERTY = "EDITED";
	private static final String INPUTED_PROPERTY = "INPUTED";
	private static final String PREVIOUS_PROPERTY = "PREVIOUS";
	private static final String COLLECTED_PROPERTY = "COLLECTED";

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
			jsonArrayQuestionnaireModelQueenLog = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//logS1Tel.json").getFile()));
			jsonArrayQuestionnaireModelStromaeLog = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//logS1Web.json").getFile()));
			jsonArrayMetadata = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//metadata//metadata.json").getFile()));

	 } catch (Exception e) {
		 e.printStackTrace();
	 }

	log.info("Dataset Logement : creation Nomenclature");

	ArrayList<Nomenclature> listNomenclature = createLogementNomenclature();

	log.info("Dataset Logement : creation Campaign Stromae");

	injectCampaign("LOG2021X11Web", "Enquête Logement 2022 - Séquence 1 - HR - Web", jsonArrayQuestionnaireModelStromaeLog,listNomenclature, jsonArrayMetadata);

	log.info("Dataset Logement : creation Campaign Queen");
	
	injectCampaign("LOG2021X11Tel", "Enquête Logement 2022 - Séquence 1 - HR", jsonArrayQuestionnaireModelQueenLog,listNomenclature,null);

	log.info("Dataset Logement : end Creation");


	}

	private void injectCampaign(String id, String label,JsonNode jsonQm, ArrayList<Nomenclature> listNomenclature,JsonNode jsonMetadata) {
		Pair<Campaign,QuestionnaireModel> campQm = createCampaign(id,label, jsonQm,jsonMetadata, listNomenclature);
		Campaign camp= campQm.getFirst();
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
			jsonArrayNomenclatureDepNais = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//nomenclatures//L_DEPNAIS.json").getFile()));
			jsonArrayNomenclatureNationEtr = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//nomenclatures//L_NATIONETR.json").getFile()));
			jsonArrayNomenclaturePaysNais = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//nomenclatures//L_PAYSNAIS.json").getFile()));
			jsonArrayNomenclatureCogCom = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//logement//nomenclatures//cog-communes.json").getFile()));
			
	 } catch (Exception e) {
		 e.printStackTrace();
	 }

		Nomenclature nomenclatureDepNais = new Nomenclature("L_DEPNAIS","départements français",jsonArrayNomenclatureDepNais.toString());
		Nomenclature nomenclatureNationEtr = new Nomenclature("L_NATIONETR","nationalités",jsonArrayNomenclatureNationEtr.toString());
		Nomenclature nomenclaturePaysNais = new Nomenclature("L_PAYSNAIS","pays",jsonArrayNomenclaturePaysNais.toString());
		Nomenclature nomenclatureCogCom = new Nomenclature("cog-communes","communes françaises",jsonArrayNomenclatureCogCom.toString());
 
		ArrayList<Nomenclature> listNomenclature = new ArrayList<>(Arrays.asList(nomenclatureDepNais,nomenclatureNationEtr,nomenclaturePaysNais,nomenclatureCogCom));
		createNomenclatures(listNomenclature);
		return listNomenclature;
	}

	private Pair<Campaign,QuestionnaireModel> createCampaign(String id, String label,JsonNode jsonQm,JsonNode jsonMetadata, ArrayList<Nomenclature> listNomenclature) {
		log.info(String.format("Create Campaing %s",id));
		Campaign camp = new Campaign(id,label,null); 
		QuestionnaireModel qm = new QuestionnaireModel(id,label,jsonQm.toString(),new HashSet<>(listNomenclature),camp);
		if(campaignservice.findById(camp.id()).isEmpty()) {
			camp.questionnaireModels(new HashSet<>(List.of(qm)));
			campaignservice.save(camp);
			if (jsonMetadata != null) {
				Metadata metadata = new Metadata(UUID.randomUUID(),jsonMetadata.toString(),camp);
				metadataService.save(metadata);
			}
			if(questionnaireModelService.findById(qm.id()).isEmpty()) {
				questionnaireModelService.save(qm);
			}
		}
		return Pair.of(camp,qm);
	}

	private void createNomenclatures(ArrayList<Nomenclature> listNomenclature) {
		log.info("Creation Nomenclatures");
		listNomenclature.forEach(nomenclature -> {
			if(nomenclatureService.findById(nomenclature.id()).isEmpty()) {
				nomenclatureService.save(nomenclature);
			}
		}
		);
	}

	private void initSurveyUnit(String id, Campaign campaign, QuestionnaireModel questionnaireModel) {
		if(surveyUnitService.findById(id).isEmpty()) {
			log.info("initSurveyUnit -> SU Do not present, we create it");
			SurveyUnit su = new SurveyUnit(id,campaign,questionnaireModel,null,null,null,null);
			surveyUnitService.save(su); // That save SU in DB which is necessary to add data, comment etc...
			Data data = new Data(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su);
			dataService.save(data);
			su.data(data);

			Comment comment = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su);
			commentService.save(comment);
			su.comment(comment);

			Personalization personalization = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su);
			personalizationService.save(personalization);
			su.personalization(personalization);

			StateData stateData = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su);
			stateDataService.save(stateData);
			
			surveyUnitService.save(su);
			log.info("End of SU Creation");
		}

	}

	private void createSimpsonVqsDataSet() {
		JsonNode jsonArrayNomenclatureCities2019 = objectMapper.createObjectNode();
		JsonNode jsonArrayRegions2019 = objectMapper.createObjectNode();
		JsonNode jsonQuestionnaireModelSimpsons = objectMapper.createObjectNode();
		JsonNode jsonQuestionnaireModelVqs = objectMapper.createObjectNode();
		try {
			 jsonArrayNomenclatureCities2019 = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//public_communes-2019.json").getFile()));
			 jsonArrayRegions2019 = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//public_regions-2019.json").getFile()));
			 jsonQuestionnaireModelSimpsons = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//simpsons.json").getFile()));
			 jsonQuestionnaireModelVqs = objectMapper.readTree(new File(getClass().getClassLoader().getResource("db//dataset//vqs.json").getFile()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Nomenclature n = createNomenclature("cities2019","french cities 2019",jsonArrayNomenclatureCities2019);
		Nomenclature n2 = createNomenclature("regions2019","french regions 2019",jsonArrayRegions2019);

		QuestionnaireModel qmWithoutCamp = new QuestionnaireModel("QmWithoutCamp","Questionnaire with no campaign",jsonQuestionnaireModelSimpsons.toString(),new HashSet<>(List.of(n)),null);
		if(questionnaireModelService.findById(qmWithoutCamp.id()).isEmpty()) {
			questionnaireModelService.save(qmWithoutCamp);
		}
	

		Campaign camp = new Campaign("SIMPSONS2020X00","Survey on the Simpsons tv show 2020",null); 
		QuestionnaireModel qm = new QuestionnaireModel("simpsons","Questionnaire about the Simpsons tv show",jsonQuestionnaireModelSimpsons.toString(),new HashSet<>(List.of(n)),camp);
		camp.questionnaireModels(new HashSet<>(List.of(qm)));
		QuestionnaireModel qmSimpsons2 = new QuestionnaireModel("simpsonsV2","Questionnaire about the Simpsons tv show version 2",jsonQuestionnaireModelSimpsons.toString(),new HashSet<>(List.of(n)),camp);
		camp.questionnaireModels(new HashSet<>(List.of(qm)));
		createCampaign1(camp, qm, qmSimpsons2);
		
		

		Campaign camp2 = new Campaign("VQS2021X00","Everyday life and health survey 2021",null);
		QuestionnaireModel qm2 = new QuestionnaireModel("VQS2021X00","Questionnaire of the Everyday life and health survey 2021",jsonQuestionnaireModelVqs.toString(),new HashSet<>(List.of(n, n2)),camp2);
		createCampaign2(camp2, qm2);
	}

	private void createCampaign2(Campaign camp2, QuestionnaireModel qm2) {
		if(campaignservice.findById(camp2.id()).isEmpty()) {
			campaignservice.save(camp2);
			if(questionnaireModelService.findById(qm2.id()).isEmpty()) {
				questionnaireModelService.save(qm2);
			}
			camp2.questionnaireModels(new HashSet<>(List.of(qm2)));
			campaignservice.save(camp2);
			Metadata md2 = new Metadata(UUID.randomUUID(),objectMapper.createObjectNode().toString(),camp2);
			metadataService.save(md2);
			
			Data d2;
			Comment c2;
			Personalization p2;
			StateData sd2;
			SurveyUnit su2 = new SurveyUnit("20",camp2,qm2,null,null,null,null);
			if(surveyUnitService.findById(su2.id()).isPresent()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.data(d2);
				su2.stateData(sd2);
				su2.comment(c2);
				su2.personalization(p2);
				surveyUnitService.save(su2);
				createParadataEvents(su2);
			}
			su2 = new SurveyUnit("21",camp2,qm2,null,null,null,null);
			if(surveyUnitService.findById(su2.id()).isEmpty()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.data(d2);
				su2.stateData(sd2);
				su2.comment(c2);
				su2.personalization(p2);
				surveyUnitService.save(su2);
				createParadataEvents(su2);
			}
			su2 = new SurveyUnit("22",camp2,qm2,null,null,null,null);
			if(surveyUnitService.findById(su2.id()).isEmpty()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.data(d2);
				su2.stateData(sd2);
				su2.comment(c2);
				su2.personalization(p2);
				surveyUnitService.save(su2);
				createParadataEvents(su2);
			}
			su2 = new SurveyUnit("23",camp2,qm2,null,null,null,null);
			if(surveyUnitService.findById(su2.id()).isEmpty()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su2);
				personalizationService.save(p2);
				su2.data(d2);
				su2.comment(c2);
				su2.personalization(p2);
				surveyUnitService.save(su2);
				createParadataEvents(su2);
			}
		}
	}

	private void createCampaign1(Campaign camp, QuestionnaireModel qm, QuestionnaireModel qm2) {
		if(campaignservice.findById(camp.id()).isEmpty()) {
			campaignservice.save(camp);
			if(questionnaireModelService.findById(qm.id()).isEmpty()) {
				questionnaireModelService.save(qm);
			}
			if(questionnaireModelService.findById(qm2.id()).isEmpty()) {
				questionnaireModelService.save(qm2);
			}
			Set<QuestionnaireModel> models = camp.questionnaireModels();
			models.add(qm);
			models.add(qm2);
			campaignservice.save(camp);

			Metadata md = new Metadata(UUID.randomUUID(),objectMapper.createObjectNode().toString(),camp);
			metadataService.save(md);
			
			Data d;
			Comment c;
			Personalization p;
			StateData sd;
			SurveyUnit su = new SurveyUnit("11",camp,qm,null,null,null,null);
			if(surveyUnitService.findById(su.id()).isEmpty()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.id()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),getComment(),su);
				commentService.save(c);
				ArrayNode pValue = objectMapper.createArrayNode();
				ObjectNode jsonObject = objectMapper.createObjectNode();
				jsonObject.put("name", "whoAnswers1");
				jsonObject.put("value", "Mr Dupond");
				pValue.add(jsonObject);
				jsonObject = objectMapper.createObjectNode();
				jsonObject.put("name", "whoAnswers2");
				jsonObject.put("value", "");
				pValue.add(jsonObject);
				p = new Personalization(UUID.randomUUID(),pValue.toString(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.EXTRACTED,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.data(d);
				su.stateData(sd);
				su.comment(c);
				su.personalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("12",camp,qm,null,null,null,null);
			if(surveyUnitService.findById(su.id()).isEmpty()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.id()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.data(d);
				su.stateData(sd);
				su.comment(c);
				su.personalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("13",camp,qm2,null,null,null,null);
			if(surveyUnitService.findById(su.id()).isEmpty()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.id()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.data(d);
				su.stateData(sd);
				su.comment(c);
				su.personalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("14",camp,qm2,null,null,null,null);
			if(surveyUnitService.findById(su.id()).isEmpty()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.id()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode().toString(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createArrayNode().toString(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.data(d);
				su.stateData(sd);
				su.comment(c);
				su.personalization(p);
				surveyUnitService.save(su);
			}
		}
	}

	private void createParadataEvents(SurveyUnit su) {
		ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
		rootNode.set("idSU", JsonNodeFactory.instance.textNode(su.id()));
		ParadataEvent pde = new ParadataEvent(UUID.randomUUID(),rootNode.toString());
		ParadataEvent pde2 = new ParadataEvent(UUID.randomUUID(),rootNode.toString());
		paradataEventService.save(pde);
		paradataEventService.save(pde2);
	}

	private Nomenclature createNomenclature(String id, String label, JsonNode jsonNomenclature) {
		Nomenclature n = new Nomenclature(id,label,jsonNomenclature.toString());
		if(nomenclatureService.findById(n.id()).isEmpty()) {
			nomenclatureService.save(n);
		}
		return n;
	}

	private String getComment() {
		ObjectNode jsonValue = objectMapper.createObjectNode();
		jsonValue.put("COMMENT", "a comment");
		return jsonValue.toString();
	}

	@SuppressWarnings("deprecation")
	private String getDataValue(String id) {
		ObjectNode jsonValue = objectMapper.createObjectNode();
		ObjectNode jsonBroadcast = objectMapper.createObjectNode();
		jsonBroadcast.put("LAST_BROADCAST", "12/07/1998");
		jsonValue.put("EXTERNAL", jsonBroadcast);
		if("11".equals(id)) {
			return jsonValue.toString();
		}

		ObjectNode collectedNode = JsonNodeFactory.instance.objectNode();

		// Create the "COMMENT" object node
		ObjectNode commentNode = JsonNodeFactory.instance.objectNode();
		commentNode.putNull(EDITED_PROPERTY);
		commentNode.putNull(FORCED_PROPERTY);
		commentNode.putNull(INPUTED_PROPERTY);
		commentNode.putNull(PREVIOUS_PROPERTY);
		commentNode.put(COLLECTED_PROPERTY, "Love it !");

		// Add "COMMENT" node to the "COLLECTED" node
		collectedNode.set("COMMENT", commentNode);


		// Create the "READY" object node
		ObjectNode readyNode = JsonNodeFactory.instance.objectNode();
		readyNode.putNull(EDITED_PROPERTY);
		readyNode.putNull(FORCED_PROPERTY);
		readyNode.putNull(INPUTED_PROPERTY);
		readyNode.putNull(PREVIOUS_PROPERTY);
		readyNode.put(COLLECTED_PROPERTY, true);

		// Add "READY" node to the "COLLECTED" node
		collectedNode.set("READY", readyNode);

		// Create the "PRODUCER" object node
		ObjectNode producerNode = JsonNodeFactory.instance.objectNode();
		producerNode.putNull(EDITED_PROPERTY);
		producerNode.putNull(FORCED_PROPERTY);
		producerNode.putNull(INPUTED_PROPERTY);
		producerNode.putNull(PREVIOUS_PROPERTY);
		producerNode.put(COLLECTED_PROPERTY, "Matt Groening");

		// Add "PRODUCER" node to the "COLLECTED" node
		collectedNode.set("PRODUCER", producerNode);

		// Add the "COLLECTED" array node to the root node
		jsonValue.set(COLLECTED_PROPERTY, collectedNode);

		return jsonValue.toString();
	}

}
