package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.dto.comment.CommentDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

	public CommentDto getComment(String surveyUnitId) {
		return commentRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Comment %s was not found for survey unit id", surveyUnitId)));
	}

	public void updateComment(String surveyUnitId, JsonNode commentValue) {
		commentRepository.updateComment(surveyUnitId, commentValue.toString());
	}
}
