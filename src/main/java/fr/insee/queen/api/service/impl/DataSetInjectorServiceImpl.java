package fr.insee.queen.api.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.ParadataEvent;
import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.CommentService;
import fr.insee.queen.api.service.DataService;
import fr.insee.queen.api.service.DataSetInjectorService;
import fr.insee.queen.api.service.MetadataService;
import fr.insee.queen.api.service.NomenclatureService;
import fr.insee.queen.api.service.ParadataEventService;
import fr.insee.queen.api.service.PersonalizationService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.StateDataService;
import fr.insee.queen.api.service.SurveyUnitService;

@Service
public class DataSetInjectorServiceImpl implements DataSetInjectorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetInjectorServiceImpl.class);

	@Autowired
	private CampaignService campaignservice;
	@Autowired
	private SurveyUnitService surveyUnitService;
	@Autowired
	private DataService dataService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private ParadataEventService paradataEventService;
	@Autowired
	private MetadataService metadataService;
	@Autowired
	private PersonalizationService personalizationService;
	@Autowired
	private StateDataService stateDataService;
	@Autowired
	private QuestionnaireModelService questionnaireModelService;
	@Autowired
	private NomenclatureService nomenclatureService;

	private static final String CURRENT_PAGE = "2.3#5";

	private ObjectMapper objectMapper = new ObjectMapper();
	private Nomenclature n;
	private Nomenclature n2;


	public void createDataSet() {
		LOGGER.info("Dataset creation start");
		createSimpsonVqsDataSet();
		createLogementDataSet();
		LOGGER.info("Dataset creation end");	
	}

	public void createLogementDataSet() {
		LOGGER.info("Dataset Queen Logement creation start");
		
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

	LOGGER.info("Dataset Logement : creation Nomenclature");

	ArrayList<Nomenclature> listNomenclature = createLogementNomenclature();

	LOGGER.info("Dataset Logement : creation Campaign Stromae");

	injectCampaign("LOG2021X11Web", "Enquête Logement 2022 - Séquence 1 - HR - Web", jsonArrayQuestionnaireModelStromaeLog,listNomenclature, jsonArrayMetadata);

	LOGGER.info("Dataset Logement : creation Campaign Queen");
	
	injectCampaign("LOG2021X11Tel", "Enquête Logement 2022 - Séquence 1 - HR", jsonArrayQuestionnaireModelQueenLog,listNomenclature,null);

	LOGGER.info("Dataset Logement : end Creation");


	}

	private void injectCampaign(String id, String label,JsonNode jsonQm, ArrayList<Nomenclature> listNomenclature,JsonNode jsonMetadata) {
		Pair<Campaign,QuestionnaireModel> campQm = createCampaign(id,label, jsonQm,jsonMetadata, listNomenclature);
		Campaign camp= campQm.getFirst();
		QuestionnaireModel qm = campQm.getSecond();
		LOGGER.info("Dataset : creation SurveyUnit");
	
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

		Nomenclature nomenclatureDepNais = new Nomenclature("L_DEPNAIS","départements français",jsonArrayNomenclatureDepNais);
		Nomenclature nomenclatureNationEtr = new Nomenclature("L_NATIONETR","nationalités",jsonArrayNomenclatureNationEtr);
		Nomenclature nomenclaturePaysNais = new Nomenclature("L_PAYSNAIS","pays",jsonArrayNomenclaturePaysNais);
		Nomenclature nomenclatureCogCom = new Nomenclature("cog-communes","communes françaises",jsonArrayNomenclatureCogCom);
 
		ArrayList<Nomenclature> listNomenclature = new ArrayList<Nomenclature>(Arrays.asList(nomenclatureDepNais,nomenclatureNationEtr,nomenclaturePaysNais,nomenclatureCogCom));
		createNomenclatures(listNomenclature);
		return listNomenclature;
	}

	private Pair<Campaign,QuestionnaireModel> createCampaign(String id, String label,JsonNode jsonQm,JsonNode jsonMetadata, ArrayList<Nomenclature> listNomenclature) {
		LOGGER.info(String.format("Create Campaing %s",id));
		Campaign camp = new Campaign(id,label,null); 
		QuestionnaireModel qm = new QuestionnaireModel(id,label,jsonQm,new HashSet<>(listNomenclature),camp);
		if(!campaignservice.findById(camp.getId()).isPresent()) {
			camp.setQuestionnaireModels(new HashSet<>(List.of(qm)));
			campaignservice.save(camp);
			if (jsonMetadata != null) {
				Metadata metadata = new Metadata(UUID.randomUUID(),jsonMetadata,camp);
				metadataService.save(metadata);
			}
			if(!questionnaireModelService.findById(qm.getId()).isPresent()) {
				questionnaireModelService.save(qm);
			}
		}
		return Pair.of(camp,qm);
	}

	private void createNomenclatures(ArrayList<Nomenclature> listNomenclature) {
		LOGGER.info("Creation Nomenclatures");
		listNomenclature.stream().forEach((nomenclature) -> {
			if(!nomenclatureService.findById(nomenclature.getId()).isPresent()) {
				nomenclatureService.save(nomenclature);
			}
		}
		);
	}

	private void initSurveyUnit(String id, Campaign campaign, QuestionnaireModel questionnaireModel) {
		if(!surveyUnitService.findById(id).isPresent()) {
			LOGGER.info("initSurveyUnit -> SU Do not present, we create it");
			SurveyUnit su = new SurveyUnit(id,campaign,questionnaireModel,null,null,null,null);
			surveyUnitService.save(su); // That save SU in DB which is necessary to add data, comment etc...
			Data data = new Data(UUID.randomUUID(),objectMapper.createObjectNode(),su);
			dataService.save(data);
			su.setData(data);

			Comment comment = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su);
			commentService.save(comment);
			su.setComment(comment);

			Personalization personalization = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su);
			personalizationService.save(personalization);
			su.setPersonalization(personalization);

			StateData stateData = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su);
			stateDataService.save(stateData);
			
			surveyUnitService.save(su);
			LOGGER.info("End of SU Creation");
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
		createNomenclatures(jsonArrayNomenclatureCities2019, jsonArrayRegions2019);
		createParadataEvents();
		
		
		QuestionnaireModel qmWithoutCamp = new QuestionnaireModel("QmWithoutCamp","Questionnaire with no campaign",jsonQuestionnaireModelSimpsons,new HashSet<>(List.of(n)),null);
		if(!questionnaireModelService.findById(qmWithoutCamp.getId()).isPresent()) {
			questionnaireModelService.save(qmWithoutCamp);
		}
	

		Campaign camp = new Campaign("SIMPSONS2020X00","Survey on the Simpsons tv show 2020",null); 
		QuestionnaireModel qm = new QuestionnaireModel("simpsons","Questionnaire about the Simpsons tv show",jsonQuestionnaireModelSimpsons,new HashSet<>(List.of(n)),camp);
		camp.setQuestionnaireModels(new HashSet<>(List.of(qm)));
		QuestionnaireModel qmSimpsons2 = new QuestionnaireModel("simpsonsV2","Questionnaire about the Simpsons tv show version 2",jsonQuestionnaireModelSimpsons,new HashSet<>(List.of(n)),camp);
		camp.setQuestionnaireModels(new HashSet<>(List.of(qm)));
		createCampaign1(camp, qm, qmSimpsons2);
		
		

		Campaign camp2 = new Campaign("VQS2021X00","Everyday life and health survey 2021",null);
		QuestionnaireModel qm2 = new QuestionnaireModel("VQS2021X00","Questionnaire of the Everyday life and health survey 2021",jsonQuestionnaireModelVqs,new HashSet<>(List.of(n, n2)),camp2);
		createCampaign2(camp2, qm2);
	}

	private void createCampaign2(Campaign camp2, QuestionnaireModel qm2) {
		if(!campaignservice.findById(camp2.getId()).isPresent()) {
			campaignservice.save(camp2);
			if(!questionnaireModelService.findById(qm2.getId()).isPresent()) {
				questionnaireModelService.save(qm2);
			}
			camp2.setQuestionnaireModels(new HashSet<>(List.of(qm2)));
			campaignservice.save(camp2);
			Metadata md2 = new Metadata(UUID.randomUUID(),objectMapper.createObjectNode(),camp2);
			metadataService.save(md2);
			
			Data d2;
			Comment c2;
			Personalization p2;
			StateData sd2;
			SurveyUnit su2 = new SurveyUnit("20",camp2,qm2,null,null,null,null);
			if(surveyUnitService.findById(su2.getId()).isPresent()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.setData(d2);
				su2.setStateData(sd2);
				su2.setComment(c2);
				su2.setPersonalization(p2);
				surveyUnitService.save(su2);
			}
			su2 = new SurveyUnit("21",camp2,qm2,null,null,null,null);
			if(!surveyUnitService.findById(su2.getId()).isPresent()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.setData(d2);
				su2.setStateData(sd2);
				su2.setComment(c2);
				su2.setPersonalization(p2);
				surveyUnitService.save(su2);
			}
			su2 = new SurveyUnit("22",camp2,qm2,null,null,null,null);
			if(!surveyUnitService.findById(su2.getId()).isPresent()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				personalizationService.save(p2);
				sd2 = new StateData(UUID.randomUUID(),StateDataType.INIT,900000000L,"1",su2);
				stateDataService.save(sd2);
				su2.setData(d2);
				su2.setStateData(sd2);
				su2.setComment(c2);
				su2.setPersonalization(p2);
				surveyUnitService.save(su2);
			}
			su2 = new SurveyUnit("23",camp2,qm2,null,null,null,null);
			if(!surveyUnitService.findById(su2.getId()).isPresent()) {
				surveyUnitService.save(su2);
				d2 = new Data(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				dataService.save(d2);
				c2 = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				commentService.save(c2);
				p2 = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su2);
				personalizationService.save(p2);
				su2.setData(d2);
				su2.setComment(c2);
				su2.setPersonalization(p2);
				surveyUnitService.save(su2);
			}
		}
	}

	private void createCampaign1(Campaign camp, QuestionnaireModel qm, QuestionnaireModel qm2) {
		if(!campaignservice.findById(camp.getId()).isPresent()) {
			campaignservice.save(camp);
			if(!questionnaireModelService.findById(qm.getId()).isPresent()) {
				questionnaireModelService.save(qm);
			}
			if(!questionnaireModelService.findById(qm2.getId()).isPresent()) {
				questionnaireModelService.save(qm2);
			}
			Set<QuestionnaireModel> models = camp.getQuestionnaireModels();
			models.add(qm);
			models.add(qm2);
			campaignservice.save(camp);

			Metadata md = new Metadata(UUID.randomUUID(),objectMapper.createObjectNode(),camp);
			metadataService.save(md);
			
			Data d;
			Comment c;
			Personalization p;
			StateData sd;
			SurveyUnit su = new SurveyUnit("11",camp,qm,null,null,null,null);
			if(!surveyUnitService.findById(su.getId()).isPresent()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.getId()),su);
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
				p = new Personalization(UUID.randomUUID(),pValue,su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.EXTRACTED,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.setData(d);
				su.setStateData(sd);
				su.setComment(c);
				su.setPersonalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("12",camp,qm,null,null,null,null);
			if(!surveyUnitService.findById(su.getId()).isPresent()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.getId()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.setData(d);
				su.setStateData(sd);
				su.setComment(c);
				su.setPersonalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("13",camp,qm2,null,null,null,null);
			if(!surveyUnitService.findById(su.getId()).isPresent()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.getId()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.setData(d);
				su.setStateData(sd);
				su.setComment(c);
				su.setPersonalization(p);
				surveyUnitService.save(su);
			}
			su = new SurveyUnit("14",camp,qm2,null,null,null,null);
			if(!surveyUnitService.findById(su.getId()).isPresent()) {
				surveyUnitService.save(su);
				d = new Data(UUID.randomUUID(),getDataValue(su.getId()),su);
				dataService.save(d);
				c = new Comment(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				commentService.save(c);
				p = new Personalization(UUID.randomUUID(),objectMapper.createObjectNode(),su);
				personalizationService.save(p);
				sd = new StateData(UUID.randomUUID(),StateDataType.INIT,1111111111L,CURRENT_PAGE,su);
				stateDataService.save(sd);
				su.setData(d);
				su.setStateData(sd);
				su.setComment(c);
				su.setPersonalization(p);
				surveyUnitService.save(su);
			}
		}
	}

	private void createParadataEvents() {
		ParadataEvent pde = new ParadataEvent(UUID.randomUUID(),objectMapper.createObjectNode());
		ParadataEvent pde2 = new ParadataEvent(UUID.randomUUID(),objectMapper.createObjectNode());
		paradataEventService.save(pde);
		paradataEventService.save(pde2);
	}

	private void createNomenclatures(JsonNode jsonArrayNomenclatureCities2019, JsonNode jsonArrayRegions2019) {
		n = new Nomenclature("cities2019","french cities 2019",jsonArrayNomenclatureCities2019);
		n2 = new Nomenclature("regions2019","french regions 2019",jsonArrayRegions2019);
		if(!nomenclatureService.findById(n.getId()).isPresent()) {
			nomenclatureService.save(n);
		}
		if(!nomenclatureService.findById(n2.getId()).isPresent()) {
			nomenclatureService.save(n2);
		}
	}

	private JsonNode getComment() {
		ObjectNode jsonValue = objectMapper.createObjectNode();
		jsonValue.put("COMMENT", "a comment");
		return jsonValue;
	}

	@SuppressWarnings("deprecation")
	private JsonNode getDataValue(String id) {
		ObjectNode jsonValue = objectMapper.createObjectNode();
		ObjectNode jsonBroadcast = objectMapper.createObjectNode();
		jsonBroadcast.put("LAST_BROADCAST", "12/07/1998");
		jsonValue.put("EXTERNAL", jsonBroadcast);
		if("11".equals(id)) {
			return jsonValue;
		}
		ObjectNode jsonCollected = objectMapper.createObjectNode();
		jsonCollected.put("COMMENT", "Love it !");
		jsonCollected.put("READY", true);
		jsonCollected.put("PRODUCER", "Matt Groening");
		jsonValue.put("COLLECTED", jsonCollected);
		return jsonValue;
	}

}
