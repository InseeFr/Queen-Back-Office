package fr.insee.queen.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.insee.queen.api.domain.SurveyUnit;
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
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.NomenclatureService;
import fr.insee.queen.api.service.UtilsService;
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
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* The nomencalture repository using to access to table 'nomenclature' in DB 
	*/
	@Autowired
	private NomenclatureService nomenclatureservice;

	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignService campaignService;

	@ApiOperation(value = "Get all nomenclatures Ids ")
	@GetMapping(path = "/nomenclatures")
	public ResponseEntity<List<String>> getNomenclaturesId() {

		LOGGER.info("GET all nomenclatures Ids resulting in 200");
		List<Nomenclature> nomenclatures = nomenclatureservice.findAll();
		if(nomenclatures.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<String> resp = nomenclatures.stream()
				.map(nomenc -> nomenc.getId())
				.collect(Collectors.toList());
		Collections.sort(resp);
		return new ResponseEntity<>(resp, HttpStatus.OK);

	}

	
	/**
	* This method is using to get the a specific Nomenclature
	* 
	* @param id the id of nomenclature
	* @return {@link NomenclatureDto} the nomenclature
	*/
	@ApiOperation(value = "Get Nomenclature by Id ")
	@GetMapping(path = "/nomenclature/{id}")
	public ResponseEntity<Object> getNomenclatureById(@PathVariable(value = "id") String id){
		Optional<Nomenclature> nomenclatureOptional = nomenclatureservice.findById(id);
		if (!nomenclatureOptional.isPresent()) {
			LOGGER.error("GET nomenclature with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET nomenclature with id {} resulting in 200", id);
			return new ResponseEntity<>(nomenclatureOptional.get().getValue(), HttpStatus.OK);
		}
		
	}
	
	/**
	* This method is using to get all nomenclature ids associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return List of {@link String} containing nomenclature ids
	 * @throws Exception 
	*/
	@ApiOperation(value = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/campaign/{id}/required-nomenclatures")
	public ResponseEntity<List<String>> getListRequiredNomenclature(@PathVariable(value = "id") String id) {
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("GET required-nomenclatures for campaign with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET required-nomenclatures for campaign with id {} resulting in 200", id);
			try {
				return new ResponseEntity<>(nomenclatureservice.findRequiredNomenclatureByCampaign(id), HttpStatus.OK);
			} catch (NotFoundException e) {
				LOGGER.info(e.getMessage());
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
	}
	
	/**
	* This method is using to get all nomenclature ids associated to a specific questionnaire 
	* 
	* @param id the id of campaign
	* @return List of {@link String} containing nomenclature ids
	 * @throws Exception 
	*/
	@ApiOperation(value = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/questionnaire/{id}/required-nomenclatures")
	public ResponseEntity<List<String>> getListRequiredNomenclatureByQuestionnaireId(@PathVariable(value = "id") String id) {
		List<String> lstRequiredNomenclature = nomenclatureservice.findRequiredNomenclatureByQuestionnaire(id);
		if(lstRequiredNomenclature == null) {
			LOGGER.error("GET required-nomenclatures for questionnaire with id {} resulting in 404", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("GET required-nomenclatures for campaign with id {} resulting in 200", id);
		return new ResponseEntity<>(lstRequiredNomenclature, HttpStatus.OK);
	}
	
	/**
	* This method is using to create or update a nomenclature
	* 
	* @param id the id of campaign
	* @return List of {@link String} containing nomenclature ids
	 * @throws Exception 
	*/
	@ApiOperation(value = "Post new  or update a nomenclature ")
	@PostMapping(path = "/nomenclature")
	public ResponseEntity<Object> postNomenclature(@RequestBody NomenclatureDto nomenclature) {
		/*if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return ResponseEntity.notFound().build();
		}*/
		nomenclatureservice.createNomenclature(nomenclature);
		return ResponseEntity.ok().build();
	}
	
	
}
