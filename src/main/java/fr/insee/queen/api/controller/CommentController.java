package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.dto.comment.CommentDto;
import fr.insee.queen.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
* CommentController is the Controller using to manage survey unit comments
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
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
	@Operation(summary = "Get comment for reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/comment")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getCommentBySurveyUnit(@PathVariable(value = "id") String surveyUnitId, Authentication auth){
		log.info("GET comment for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		return commentService.getComment(surveyUnitId).value();
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param surveyUnitId	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* 
	*/
	@Operation(summary = "Update the comment by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/comment")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public HttpStatus setComment(@RequestBody JsonNode commentValue,
								 @PathVariable(value = "id") String surveyUnitId,
								 Authentication auth) {
		log.info("PUT comment for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		commentService.updateComment(surveyUnitId, commentValue);
		return HttpStatus.OK;
	}	
}
