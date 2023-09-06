package fr.insee.queen.api.controller;

import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.service.NomenclatureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* NomenclatureController is the Controller using to manage nomenclatures
*
* @author Claudel Benjamin
*
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
public class NomenclatureController {

	/**
	* The nomencalture repository using to access to table 'nomenclature' in DB
	*/
	private final NomenclatureService nomenclatureService;

	@Operation(summary = "Get all nomenclatures Ids ")
	@GetMapping(path = "/nomenclatures")
	public List<String> getNomenclaturesId() {
		log.info("GET all nomenclatures Ids");
		return nomenclatureService.getAllNomenclatureIds();
	}


	/**
	* This method is using to get the a specific Nomenclature
	*
	* @param nomenclatureId the id of nomenclature
	* @return {@link String} the nomenclature
	*/
	@Operation(summary = "Get Nomenclature by Id ")
	@GetMapping(path = "/nomenclature/{id}")
	public String getNomenclatureById(@PathVariable(value = "id") String nomenclatureId){
		log.info("GET nomenclature with id {}", nomenclatureId);
		return nomenclatureService.getNomenclature(nomenclatureId).value();

	}

	/**
	* This method is using to get all nomenclature ids associated to a specific campaign
	*
	* @param campaignId the id of campaign
	* @return List of {@link String} containing nomenclature ids
	*/
	@Operation(summary = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/campaign/{id}/required-nomenclatures")
	public List<String> getListRequiredNomenclature(@PathVariable(value = "id") String campaignId) {
		log.info("GET required-nomenclatures for campaign with id {}", campaignId);
		return nomenclatureService.findRequiredNomenclatureByCampaign(campaignId);
	}

	/**
	* This method is using to get all nomenclature ids associated to a specific questionnaire
	*
	* @param questionnaireId the id of campaign
	* @return List of {@link String} containing nomenclature ids
	*/
	@Operation(summary = "Get list of required nomenclature by campaign Id ")
	@GetMapping(path = "/questionnaire/{id}/required-nomenclatures")
	public List<String> getListRequiredNomenclatureByQuestionnaireId(@PathVariable(value = "id") String questionnaireId) {
		log.info("GET required-nomenclatures for questionnaire model with id {}", questionnaireId);
		List<String> requiredNomenclatureIds = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
		if(requiredNomenclatureIds.isEmpty()) {
			throw new EntityNotFoundException(String.format("No required nomenclatures found for questionnaire %s", questionnaireId));
		}
		return requiredNomenclatureIds;
	}

	/**
	* This method is using to create or update a nomenclature
	*
	* @param nomenclatureInputDto nomenclature to create
	*/
	@Operation(summary = "Post new  or update a nomenclature ")
	@PostMapping(path = "/nomenclature")
	public void postNomenclature(@RequestBody NomenclatureInputDto nomenclatureInputDto) {
		nomenclatureService.saveNomenclature(nomenclatureInputDto);
	}
}
