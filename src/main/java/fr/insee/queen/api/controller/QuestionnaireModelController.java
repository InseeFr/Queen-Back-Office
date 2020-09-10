package fr.insee.queen.api.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
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

	/**
	* The questionnaire model repository using to access to table 'questionnaire_model' in DB 
	*/
	@Autowired
	private QuestionnaireModelRepository questionnaireModelRepository;
	
	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignRepository campaignRepository;
	
	/**
	* This method is using to get the questionnaireModel associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return the {@link QuestionnaireModelDto} associated to the campaign
	*/
	@ApiOperation(value = "Get questionnnaire model by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaire")
	public ResponseEntity<QuestionnaireModelDto> getQuestionnaireModelByCampaignId(@PathVariable(value = "id") String id){
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("GET questionnaire model for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET questionnaire model for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(questionnaireModelRepository.findQuestionnaireModelDtoByCampaignId(id), HttpStatus.OK);
		}
	}
	
	/**
	* This method is using to get the questionnaireModel Id associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return the {@link QuestionnaireIdDto} associated to the campaign
	*/
	@ApiOperation(value = "Get questionnnaire id by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaire-id")
	public ResponseEntity<QuestionnaireIdDto> getQuestionnaireModelIdByCampaignId(@PathVariable(value = "id") String id){
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("GET questionnaire Id for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET questionnaire Id for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(questionnaireModelRepository.findQuestionnaireIdDtoByCampaignId(id), HttpStatus.OK);
		}
	}
}
