package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentApiService implements CommentService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public String getComment(String surveyUnitId) {
        return surveyUnitRepository
                .findComment(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment not found for survey unit %s", surveyUnitId)));
    }

    @Override
    public void updateComment(String surveyUnitId, JsonNode commentValue) {
        surveyUnitRepository.saveComment(surveyUnitId, commentValue.toString());
    }
}
