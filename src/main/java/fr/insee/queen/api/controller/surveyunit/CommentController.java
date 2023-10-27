package fr.insee.queen.api.controller.surveyunit;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.comment.CommentDto;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import fr.insee.queen.api.service.surveyunit.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* CommentController is the Controller using to manage survey unit comments
* 
* @author Claudel Benjamin
* 
*/
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class CommentController {
	
	/**
	* The comment repository using to access to table 'comment' in DB 
	*/
	private final CommentService commentService;
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	private final HabilitationComponent habilitationComponent;
	
	/**
	* This method is using to get the comment associated to a specific reporting unit 
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link CommentDto} the comment associated to the reporting unit
	*/
	@Operation(summary = "Get comment for a survey unit")
	@GetMapping(path = "/survey-unit/{id}/comment")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getCommentBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId,
										 Authentication auth){
		log.info("GET comment for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		return commentService.getComment(surveyUnitId);
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param surveyUnitId	the id of reporting unit
	*
	*/
	@Operation(summary = "Update comment for a survey unit")
	@PutMapping(path = "/survey-unit/{id}/comment")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void setComment(@NotNull @RequestBody ObjectNode commentValue,
						   @IdValid @PathVariable(value = "id") String surveyUnitId,
						   Authentication auth) {
		log.info("PUT comment for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		commentService.updateComment(surveyUnitId, commentValue);
	}
}
