package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.service.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentApiService implements CommentService {
    private final SurveyUnitRepository surveyUnitRepository;

	public String getComment(String surveyUnitId) {
		return surveyUnitRepository.getComment(surveyUnitId);
	}

	public void updateComment(String surveyUnitId, JsonNode commentValue) {
		surveyUnitRepository.updateComment(surveyUnitId, commentValue.toString());
	}
}
