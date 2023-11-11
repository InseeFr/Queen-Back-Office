package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;


public interface CommentService {
	String getComment(String surveyUnitId);
	void updateComment(String surveyUnitId, JsonNode commentValue);
}
