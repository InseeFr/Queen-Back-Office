package fr.insee.queen.api.controller.campaign;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.service.campaign.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MetadataController is the Controller using to manage metadatas
 * entity
 * 
 * @author Corcaud Samuel
 * 
 */
@RestController
@Tag(name = "05. Metadata", description = "Endpoints for retrieving metadata")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class MetadataController {

	private final MetadataService metadataService;
	
	/**
	* This method is using to get the metadata associated to a specific campaign 
	* 
	* @param campaignId the id of the campaign
	* @return the metadata associated to the reporting unit
	*/
	@Operation(summary = "Get metadata for a specific campaign ")
	@GetMapping(path = "/campaign/{id}/metadata")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getMetadataByCampaignId(@IdValid @PathVariable(value = "id") String campaignId){
		log.info("GET metadata for campaign with id {}", campaignId);
		return metadataService.getMetadata(campaignId).value();
	}
	
	/**
	* This method is using to get the metadata associated to a specific questionnaire 
	* 
	* @param questionnaireId the id of the campaign
	* @return the metadata associated to the reporting unit
	*/
	@Operation(summary = "Get metadata for a specific questionnaire ")
	@GetMapping(path = "/questionnaire/{id}/metadata")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getMetadataByQuestionnaireId(@IdValid @PathVariable(value = "id") String questionnaireId) {
		log.info("GET metadata for questionnaire with id {}", questionnaireId);
		return metadataService.getMetadataByQuestionnaireId(questionnaireId).value();
	}
}
