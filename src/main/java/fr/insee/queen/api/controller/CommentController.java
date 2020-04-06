package fr.insee.queen.api.controller;

import java.sql.SQLException;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
* CommentController is the Controller using to manage {@link Comment} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class CommentController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
	
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
	@ApiOperation(value = "Get comment for reporting unit Id ")
	@GetMapping(path = "/reporting-unit/{id}/comment")
	public CommentDto getCommentByReportingUnit(@PathVariable(value = "id") Long id){
		LOGGER.info("GET comment for reporting unit with id {}", id);
		return commentRepository.findDtoByReportingUnit_id(id);
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Update the comment by reporting unit Id ")
	@PutMapping(path = "/reporting-unit/{id}/comment")
	public ResponseEntity<Object> setComment(@RequestBody JSONObject commentValue, @PathVariable(value = "id") Long id) throws ParseException, SQLException {
		Optional<Comment> commentOptional = commentRepository.findByReportingUnit_id(id);
		if (!commentOptional.isPresent()) {
			LOGGER.info("PUT comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {  
			commentRepository.updateValue(commentValue.toJSONString().toString(), commentOptional.get().getId());
			LOGGER.info("PUT comment for reporting unit with id {} resulting in 200", id);
			return ResponseEntity.ok().build();
		}
	}	
}
