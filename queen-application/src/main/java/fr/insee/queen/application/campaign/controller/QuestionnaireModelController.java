package fr.insee.queen.application.campaign.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.campaign.dto.input.QuestionnaireModelCreationData;
import fr.insee.queen.application.campaign.dto.output.QuestionnaireModelIdDto;
import fr.insee.queen.application.campaign.dto.output.QuestionnaireModelValueDto;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationOkNokDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * Handle questionnaire models. A questionnaire model is the definition of a questionnaire with its data internal structure
 */
@RestController
@Tag(name = "03. Questionnaires", description = "Endpoints for questionnaires")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class QuestionnaireModelController {
    private final InterrogationService interrogationService;
    private final QuestionnaireModelService questionnaireModelService;

    /**
     * Retrieve the data structure of all questionnaires linked to a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link QuestionnaireModelValueDto} linked to the campaign
     */
    @Operation(summary = "Get questionnaire list for a campaign ")
    @GetMapping(path = "/campaign/{id}/questionnaires")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<QuestionnaireModelValueDto> getQuestionnaireDatasByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        return questionnaireModelService
                .getQuestionnaireDatas(campaignId).stream()
                .map(QuestionnaireModelValueDto::new)
                .toList();
    }

    /**
     * @deprecated
     * Retrieve the data structure of a questionnaire
     *
     * @param questionnaireModelId the id of questionnaire
     * @return the {@link QuestionnaireModelValueDto} linked to the id
     */
    @Operation(summary = "Get questionnnaire")
    @GetMapping(path = "/questionnaire/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @Deprecated(since = "4.2.12")
    public QuestionnaireModelValueDto getQuestionnaireValue(@IdValid @PathVariable(value = "id") String questionnaireModelId) {
        return new QuestionnaireModelValueDto(getQuestionnaireData(questionnaireModelId));
    }

    /**
     * Retrieve the data structure of a questionnaire
     *
     * @param questionnaireModelId the id of questionnaire
     * @return the data linked to the questionnaire
     */
    @Operation(summary = "Get questionnnaire data")
    @GetMapping(path = "/questionnaire/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ObjectNode getQuestionnaireData(@IdValid @PathVariable(value = "id") String questionnaireModelId) {
        return questionnaireModelService.getQuestionnaireData(questionnaireModelId);
    }

    /**
     * Retrieve all the questionnaire ids for a campaign
     *
     * @param campaignId the campaign id
     * @return List of {@link QuestionnaireModelIdDto} list linked to the campaign id
     */
    @Operation(summary = "Get list of questionnaire ids for a campaign")
    @GetMapping(path = "/campaign/{id}/questionnaire-id")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<QuestionnaireModelIdDto> getQuestionnaireIdsByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        return questionnaireModelService
                .getQuestionnaireIds(campaignId)
                .stream()
                .map(QuestionnaireModelIdDto::new)
                .toList();
    }

    /**
     * Create a questionnaire model
     *
     * @param questionnaireModelData questionnaire data used to create a questionnaire
     */
    @Operation(summary = "Create a Questionnaire Model")
    @PostMapping(path = "/questionnaire-models")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestionnaire(@RequestBody @Valid QuestionnaireModelCreationData questionnaireModelData) {
        questionnaireModelService.createQuestionnaire(QuestionnaireModelCreationData.toModel(questionnaireModelData));
    }

    /**
     * Search questionnaire ids linked to interrogations
     *
     * @param interrogationIdsToSearch interrogation ids where we want to retrive the questionnaire ids
     * @return {@link InterrogationOkNokDto} list of interrogations with their questionnaire ids, and list of interrogations where no questionnaire found
     */
    @Operation(summary = "Search questionnaire ids linked to interrogations")
    @PostMapping("/interrogations/questionnaire-model-id")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public ResponseEntity<InterrogationOkNokDto> getQuestionnaireModelIdByInterrogations(
            @NotEmpty @RequestBody List<String> interrogationIdsToSearch) {
        List<InterrogationSummary> interrogationsFound = interrogationService.findSummariesByIds(interrogationIdsToSearch);
        List<String> interrogationIdsFound = interrogationsFound.stream().map(InterrogationSummary::id).toList();
        List<InterrogationDto> interrogationsNOK = interrogationIdsToSearch.stream()
                .filter(interrogationIdToSearch -> !interrogationIdsFound.contains(interrogationIdToSearch))
                .map(InterrogationDto::createInterrogationNOKDto)
                .toList();
        List<InterrogationDto> interrogationsOK = interrogationsFound.stream()
                .map(su -> InterrogationDto.createInterrogationOKDtoWithQuestionnaireModel(su.id(), su.questionnaireId()))
                .toList();
        return new ResponseEntity<>(new InterrogationOkNokDto(interrogationsOK, interrogationsNOK), HttpStatus.OK);
    }
}
