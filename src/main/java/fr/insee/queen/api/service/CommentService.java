package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.SurveyUnit;

public interface CommentService extends BaseService<Comment, UUID> {

	Optional<Comment> findBySurveyUnitId(String id);

	void save(Comment comment);
	
	public void updateComment(SurveyUnit su, JsonNode commentValue);
    
}
