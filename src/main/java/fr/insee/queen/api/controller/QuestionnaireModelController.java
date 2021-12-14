package fr.insee.queen.api.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCreateDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.NomenclatureService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* QuestionnaireModelController is the Controller using to manage {@link QuestionnaireModel} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class QuestionnaireModelController {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireModelController.class);
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* The questionnaire model repository using to access to table 'questionnaire_model' in DB 
	*/
	@Autowired
	private QuestionnaireModelService questionnaireModelService;
	
	/**
	* The nomenclature service using to access to table 'nomenclature' in DB 
	*/
	@Autowired
	private NomenclatureService nomenclatureService;
	
	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignService campaignService;
	
	
	/**
	* This method is using to get the questionnaireModel associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return the {@link QuestionnaireModelDto} associated to the campaign
	 * @throws NotFoundException 
	*/
	@ApiOperation(value = "Get questionnnaire model by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaires")
	public ResponseEntity<List<QuestionnaireModelDto>> getQuestionnaireModelByCampaignId(@PathVariable(value = "id") String id) {
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("GET questionnaire for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			List<QuestionnaireModelDto> resp = new ArrayList<>();
			try {
				resp = campaignService.getQuestionnaireModels(id);
			} catch (NotFoundException e) {
				LOGGER.error(e.getMessage());
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET questionnaire for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
	}

	/**
	* This method is used to retrieve a questionnaireModel by Id
	* 
	* @param id the id of questionnaire
	* @return the {@link QuestionnaireModelDto} associated to the id
	*/
	@ApiOperation(value = "Get a questionnnaire model by Id ")
	@GetMapping(path = "/questionnaire/{id}")
	public ResponseEntity<QuestionnaireModelDto> getQuestionnaireModelById(@PathVariable(value = "id") String id){
		Optional<QuestionnaireModel> questModel = questionnaireModelService.findById(id);
		if (!questModel.isPresent()) {
			LOGGER.error("GET questionnaire for id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			QuestionnaireModelDto questMod = new QuestionnaireModelDto(questModel.get());
			LOGGER.info("GET questionnaire for id {} resulting in 200", id);
			return new ResponseEntity<>(questMod, HttpStatus.OK);
		}
	}
	
	/**
	* This method is used to retrieve a questionnaireModel by Id
	* 
	* @param id the id of questionnaire
	* @return the {@link QuestionnaireModelResponseDto} associated to the id
	 * @throws NotFoundException 
	*/
	@ApiOperation(value = "Get questionnnaire id by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaire-id")
	public ResponseEntity<List<QuestionnaireIdDto>> getQuestionnaireModelIdByCampaignId(@PathVariable(value = "id") String id) {
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("GET questionnaire Id for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			List<QuestionnaireIdDto> resp = new ArrayList<>();
			try {
				resp = campaignService.getQuestionnaireIds(id);
			} catch (NotFoundException e) {
				LOGGER.error(e.getMessage());
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET questionnaire Id for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
	}
	
	/**
	* This method is using to post a new Questionnaire Model
	* 
	* @param questionnaire to create
	* @return {@link HttpStatus 400} if nomenclature is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Create a Questionnaire Model")
	@PostMapping(path = "/questionnaire-models")
	public ResponseEntity<Object> createQuestionnaire(@RequestBody QuestionnaireModelCreateDto questionnaireModel, HttpServletRequest request) {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return ResponseEntity.notFound().build();
		}
		Optional<QuestionnaireModelDto> questMod = questionnaireModelService.findDtoById(questionnaireModel.getIdQuestionnaireModel());
		if (questMod.isPresent()) {
			LOGGER.error("POST questionnaire with id {} resulting in 400 because it already exists", questionnaireModel.getIdQuestionnaireModel());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(!questionnaireModel.getRequiredNomenclatureIds().isEmpty() &&
				Boolean.FALSE.equals(nomenclatureService.checkIfNomenclatureExists(questionnaireModel.getRequiredNomenclatureIds()))) {
			LOGGER.error("POST questionnaire with id {} resulting in 400 because a nomenclature does not exist", questionnaireModel.getIdQuestionnaireModel());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		questionnaireModelService.createQuestionnaire(questionnaireModel);
		LOGGER.info("POST campaign with id {} resulting in 200", questionnaireModel.getIdQuestionnaireModel());
		return ResponseEntity.ok().build();
		
	}
}
