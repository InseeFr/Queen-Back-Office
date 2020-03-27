package fr.insee.queen.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.dto.comment.CommentDto;
import fr.insee.queen.api.repository.CommentRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
* CommentController is the Controller using to manage {@link Comment} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping
public class CommentController {
	/**
	* The comment repository using to access to table 'comment' in DB 
	*/
	@Autowired
	private CommentRepository commentRepository;
	
	/**
	* This method is using to get the comment associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link CommentDto} the comment associated to the reporting unit
	*/
	@ApiOperation(value = "Get comment by reporting unit Id ")
	@GetMapping(path = "/reporting-unit/{id}/comment")
	public CommentDto getCommentByReportingUnit(@PathVariable(value = "id") Long id){
		return commentRepository.findDtoByReportingUnit_id(id);
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* 
	*/
	@ApiOperation(value = "Update the comment by reporting unit Id ")
	@PutMapping(path = "/reporting-unit/{id}/comment")
	public ResponseEntity<Object> setComment(@RequestBody String commentValue, @PathVariable(value = "id") Long id) {
		Optional<Comment> commentOptional = commentRepository.findByReportingUnit_id(id);
		if (!commentOptional.isPresent())
			return ResponseEntity.notFound().build();
		else {
			commentOptional.get().setValue(commentValue);
			commentRepository.save(commentOptional.get());
			return ResponseEntity.ok().build();
		}
	}	
}
