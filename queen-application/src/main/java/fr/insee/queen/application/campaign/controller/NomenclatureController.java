package fr.insee.queen.application.campaign.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.campaign.dto.input.NomenclatureCreationData;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.campaign.service.NomenclatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle nomenclatures used in questionnaires
 */
@RestController
@Tag(name = "04. Nomenclatures", description = "Endpoints for nomenclatures")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class NomenclatureController {

    private final NomenclatureService nomenclatureService;

    /**
     * Retrieve all nomenclatures ids
     *
     * @return list of all nomenclature ids
     */
    @Operation(summary = "Get all nomenclatures Ids ")
    @GetMapping(path = "/nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public List<String> getNomenclaturesId() {
        return nomenclatureService.getAllNomenclatureIds();
    }


    /**
     * Retrieve a nomenclature
     *
     * @param nomenclatureId the id of nomenclature
     * @return {@link ArrayNode} the nomenclature in json format
     */
    @Operation(summary = "Get Nomenclature")
    @GetMapping(path = "/nomenclature/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.NOMENCLATURE))})
    public ArrayNode getNomenclatureById(@IdValid @PathVariable(value = "id") String nomenclatureId) {
        return nomenclatureService.getNomenclature(nomenclatureId).value();
    }

    /**
     * Create/update a nomenclature
     *
     * @param nomenclatureCreationDto nomenclature data used for nomenclature creation
     */
    @Operation(summary = "Create/update a nomenclature ")
    @PostMapping(path = "/nomenclature")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.OK)
    public void postNomenclature(@Valid @RequestBody NomenclatureCreationData nomenclatureCreationDto) {
        nomenclatureService.saveNomenclature(NomenclatureCreationData.toModel(nomenclatureCreationDto));
    }

    /**
     * Retrieve all required nomenclatures linked to a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link String} nomenclature ids
     */
    @Operation(summary = "Get list of required nomenclatures for a campaign")
    @GetMapping(path = "/campaign/{id}/required-nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public List<String> getListRequiredNomenclature(@IdValid @PathVariable(value = "id") String campaignId) {
        return nomenclatureService.findRequiredNomenclatureByCampaign(campaignId);
    }

    /**
     * Retrieve required nomenclatures linked to a questionnaire
     *
     * @param questionnaireId the id of questionnaire
     * @return List of {@link String} nomenclature ids
     */
    @Operation(summary = "Get list of required nomenclature for a questionnaire")
    @GetMapping(path = "/questionnaire/{id}/required-nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public List<String> getListRequiredNomenclatureByQuestionnaireId(@IdValid @PathVariable(value = "id") String questionnaireId) {
        return nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
    }
}
