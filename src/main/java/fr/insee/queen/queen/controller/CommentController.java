package fr.insee.queen.queen.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.domain.Comment;
import fr.insee.queen.queen.dto.operation.CommentDto;
import fr.insee.queen.queen.repository.CommentRepository;

@RestController
@RequestMapping
public class CommentController {
	@Autowired
	private CommentRepository commentRepository;
	
	@GetMapping(path = "/reporting-unit/{id}/comment")
	public CommentDto getCommentByReportingUnit(@PathVariable(value = "id") Long id){
		return commentRepository.findDtoByReportingUnit_id(id);
	}
	
	@PutMapping(path = "/reporting-unit/{id}/comment")
	public ResponseEntity<Object> setVersion(@RequestBody Comment comment,@PathVariable(value = "id") Long id) {
		Optional<Comment> commentOptional = commentRepository.findByReportingUnit_id(id);
		if (!commentOptional.isPresent())
			return ResponseEntity.notFound().build();
		else {
			commentOptional.get().setValue(comment.getValue());
			commentRepository.save(commentOptional.get());
			return ResponseEntity.ok().build();
		}
		
		
		
		
	}	
}
