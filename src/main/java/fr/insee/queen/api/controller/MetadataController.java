package fr.insee.queen.api.controller;

import java.util.Optional;

import org.bouncycastle.asn1.cms.MetaData;
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
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.MetadataService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import io.swagger.annotations.ApiOperation;

/**
 * MetadataController is the Controller using to manage {@link MetaData}
 * entity
 * 
 * @author Corcaud Samuel
 * 
 */
@RestController
@RequestMapping(path = "/api")
public class MetadataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	CampaignService campaignService;	
	
	@Autowired
	QuestionnaireModelService questionnaireModelService;
	
	@Autowired
	MetadataService metadataService;
	
	/**
	* This method is using to get the metadata associated to a specific campaign 
	* 
	* @param id the id of the campaign
	* @return {@link metaData} the metadata associated to the reporting unit
	*/
	@ApiOperation(value = "Get metadata by campaign Id ")
	@GetMapping(path = "/campaign/{id}/metadata")
	public ResponseEntity<Object>  getMetadataByCampaignId(@PathVariable(value = "id") String id){
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("GET metadata for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		MetadataDto metadataDto;
		try {
			metadataDto = metadataService.findDtoByCampaignId(id);
		} catch (NotFoundException e) {
			LOGGER.error("GET metadata for campaign with id {} resulting in 404 : No metadata found for campaign. \\n {}", id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("GET metadata for campaign with id {} resulting in 200", id);
		return new ResponseEntity<>(metadataDto.getValue(), HttpStatus.OK);
	}
	
	/**
	* This method is using to get the metadata associated to a specific questionnaire 
	* 
	* @param id the id of the campaign
	* @return {@link metaData} the metadata associated to the reporting unit
	 * @throws NotFoundException 
	*/
	@ApiOperation(value = "Get metadata by questionnaire Id ")
	@GetMapping(path = "/questionnaire/{id}/metadata")
	public ResponseEntity<Object>  getMetadataByQuestionnaireId(@PathVariable(value = "id") String id) {
		Optional<QuestionnaireModel> questionnaireOptional = questionnaireModelService.findById(id);
		if (!questionnaireOptional.isPresent()) {
			LOGGER.error("GET metadata for questionnaire with id {} resulting in 404 : Questionnaire not found", id);
			return ResponseEntity.notFound().build();
		}
		if(questionnaireOptional.get().getCampaign()==null) {
			LOGGER.error("GET metadata for questionnaire with id {} resulting in 404 : No campaign associated to questionnaire", id);
			return ResponseEntity.notFound().build();
		}
		MetadataDto metadataDto;
		try {
			metadataDto = metadataService.findDtoByCampaignId(questionnaireOptional.get().getCampaign().getId());
		} catch(NotFoundException e) {
			LOGGER.error("GET metadata for questionnaire with id {} resulting in 404 : No metadate found for campaign. \n {}", id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("GET metadata for questionnaire with id {} resulting in 200", id);
		return new ResponseEntity<>(metadataDto.getValue(), HttpStatus.OK);
	}
}
