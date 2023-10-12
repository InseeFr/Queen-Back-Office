package fr.insee.queen.api.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitOkNokDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.NomenclatureService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QuestionnaireModelController is the Controller using to manage questionnaire models
 * 
 * @author Claudel Benjamin
 * 
 */
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class QuestionnaireModelController {

	private final SurveyUnitService surveyUnitService;
	/**
	 * The questionnaire model repository using to access to table
	 * 'questionnaire_model' in DB
	 */
	private final QuestionnaireModelService questionnaireModelService;
	/**
	 * The nomenclature service using to access to table 'nomenclature' in DB
	 */
	private final NomenclatureService nomenclatureService;

	/**
	 * The campaign repository using to access to table 'campaign' in DB
	 */
	private final CampaignService campaignService;

	/**
	 * This method is using to get the questionnaireModel associated to a specific
	 * campaign
	 * 
	 * @param campaignId the id of campaign
	 * @return the {@link QuestionnaireModelDto} associated to the campaign
	 */
	@Operation(summary = "Get questionnnaire model by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaires")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<QuestionnaireModelDto> getQuestionnaireModelByCampaignId(
			@IdValid @PathVariable(value = "id") String campaignId) {
		log.info("GET questionnaire for campaign with id {}", campaignId);
		return campaignService.getQuestionnaireModels(campaignId);
	}

	/**
	 * This method is used to retrieve a questionnaireModel by Id
	 * 
	 * @param questionnaireModelId the id of questionnaire
	 * @return the {@link QuestionnaireModelDto} associated to the id
	 */
	@Operation(summary = "Get a questionnnaire model by Id ")
	@GetMapping(path = "/questionnaire/{id}")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public QuestionnaireModelDto getQuestionnaireModelById(@IdValid @PathVariable(value = "id") String questionnaireModelId) {
		log.info("GET questionnaire for id {}", questionnaireModelId);
		return questionnaireModelService.getQuestionnaireModelDto(questionnaireModelId);
	}

	/**
	 * This method is used to retrieve  questionnaireModel list by a campaign id
	 * 
	 * @param campaignId the id of questionnaire
	 * @return the {@link QuestionnaireModelIdDto} list associated to the campaign id
	 */
	@Operation(summary = "Get questionnnaire id by campaign Id ")
	@GetMapping(path = "/campaign/{id}/questionnaire-id")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<QuestionnaireModelIdDto> getQuestionnaireModelIdByCampaignId(
			@IdValid @PathVariable(value = "id") String campaignId) {
		log.info("GET questionnaire Id for campaign with id {}", campaignId);
		return campaignService.getQuestionnaireIds(campaignId);
	}

	/**
	 * This method is using to post a new Questionnaire Model
	 * 
	 * @param questionnaireModelInput to create
	 * 
	 */
	@Operation(summary = "Create a Questionnaire Model")
	@PostMapping(path = "/questionnaire-models")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	@ResponseStatus(HttpStatus.CREATED)
	public void createQuestionnaire(@RequestBody QuestionnaireModelInputDto questionnaireModelInput) {
		log.info("POST Questionnaire Model with id {}", questionnaireModelInput.idQuestionnaireModel());
		questionnaireModelService.createQuestionnaire(questionnaireModelInput);
	}

	/**
	 * This method is using to get all nomenclature ids associated to a specific questionnaire
	 *
	 * @param questionnaireId the id of questionnaire
	 * @return List of {@link String} containing nomenclature ids
	 */
	@Operation(summary = "Get list of required nomenclature by questionnaire Id ")
	@GetMapping(path = "/questionnaire/{id}/required-nomenclatures")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<String> getListRequiredNomenclatureByQuestionnaireId(@IdValid @PathVariable(value = "id") String questionnaireId) {
		log.info("GET required-nomenclatures for questionnaire model with id {}", questionnaireId);
		return questionnaireModelService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
	}



	@Operation(summary = "Get questionnaireModelId for all survey-units defined in request body ")
	@PostMapping(path = "/survey-units/questionnaire-model-id")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public ResponseEntity<SurveyUnitOkNokDto> getQuestionnaireModelIdBySurveyUnits(
			@NotEmpty @RequestBody List<String> surveyUnitIdsToSearch) {
		List<SurveyUnitSummaryDto> surveyUnitsFound = surveyUnitService.findSummaryByIds(surveyUnitIdsToSearch);
		List<String> surveyUnitIdsFound = surveyUnitsFound.stream().map(SurveyUnitSummaryDto::id).toList();
		List<SurveyUnitDto> surveyUnitsNOK = surveyUnitIdsToSearch.stream()
				.filter(surveyUnitIdToSearch -> !surveyUnitIdsFound.contains(surveyUnitIdToSearch))
				.map(SurveyUnitDto::createSurveyUnitNOKDto)
				.toList();
		List<SurveyUnitDto> surveyUnitsOK = surveyUnitsFound.stream()
				.map(su -> SurveyUnitDto.createSurveyUnitOKDtoWithQuestionnaireModel(su.id(), su.questionnaireId()))
				.toList();
		return new ResponseEntity<>(new SurveyUnitOkNokDto(surveyUnitsOK, surveyUnitsNOK), HttpStatus.OK);
	}
}
