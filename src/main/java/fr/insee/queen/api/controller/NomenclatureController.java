package fr.insee.queen.api.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.repository.CampaignRepository;
import io.swagger.annotations.ApiOperation;

/**
* NomenclatureController is the Controller using to manage {@link Nomenclature} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class NomenclatureController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NomenclatureController.class);
	
	/**
	* The nomencalture repository using to access to table 'nomenclature' in DB 
	*/
	@Autowired
	private NomenclatureRepository nomenclatureRepository;

	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignRepository campaignRepository;
	
	/**
	* The questionnaire repository using to access to table 'questionnaire' in DB 
	*/
	@Autowired
	private QuestionnaireModelRepository questionnaireModelRepository;
	
	/**
	* This method is using to get the a specific Nomenclature
	* 
	* @param id the id of nomenclature
	* @return {@link NomenclatureDto} the nomenclature
	*/
	@ApiOperation(value = "Get Nomenclature by Id ")
	@GetMapping(path = "/nomenclature/{id}")
	public ResponseEntity<Object> getNomenclatureById(@PathVariable(value = "id") String id){
		Optional<Nomenclature> nomenclatureOptional = nomenclatureRepository.findById(id);
		if (!nomenclatureOptional.isPresent()) {
			LOGGER.info("GET nomenclature with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET nomenclature with id {} resulting in 200", id);
			return new ResponseEntity<Object>(nomenclatureOptional.get().getValue(), HttpStatus.OK);
		}
		
	}
	
	/**
	* This method is using to get all nomenclature ids associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return List of {@link String} containing nomenclature ids
	*/
	@ApiOperation(value = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/campaign/{id}/required-nomenclatures")
	public ResponseEntity<Object> getListRequiredNomenclature(@PathVariable(value = "id") String id){
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("GET required-nomenclatures for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET required-nomenclatures for campaign with id {} resulting in 200", id);
			return new ResponseEntity<Object>(nomenclatureRepository.findRequiredNomenclatureByCampaign(id), HttpStatus.OK);
		}
	}
	
	/**
	* This method is using to get all nomenclature ids associated to a specific questionnaire 
	* 
	* @param id the id of campaign
	* @return List of {@link String} containing nomenclature ids
	*/
	@ApiOperation(value = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/questionnaire/{id}/required-nomenclatures")
	public ResponseEntity<Object> getListRequiredNomenclatureByQuestionnaireId(@PathVariable(value = "id") String id){
		Optional<QuestionnaireModel> questionnaireOptional = questionnaireModelRepository.findById(id);
		if (!questionnaireOptional.isPresent()) {
			LOGGER.info("GET required-nomenclatures for questionnaire with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET required-nomenclatures for campaign with id {} resulting in 200", id);
			return new ResponseEntity<Object>(questionnaireOptional.get().getNomenclatures().stream().map(Nomenclature::getLabel).collect(Collectors.toList()), 
					HttpStatus.OK);
		}
	}
}
