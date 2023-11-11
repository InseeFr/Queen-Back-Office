package fr.insee.queen.api.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.surveyunit.service.gateway.SurveyUnitRepository;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentApiService implements CommentService {
    private final SurveyUnitRepository surveyUnitRepository;

    public String getComment(String surveyUnitId) {
        return surveyUnitRepository
                .findComment(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment not found for survey unit %s", surveyUnitId)));
    }

    public void updateComment(String surveyUnitId, JsonNode commentValue) {
        surveyUnitRepository.updateComment(surveyUnitId, commentValue.toString());
    }
}
