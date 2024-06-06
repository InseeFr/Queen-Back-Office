package fr.insee.queen.domain.surveyunit.model;


import com.fasterxml.jackson.databind.node.ArrayNode;

public record SurveyUnitPersonalization(
        String surveyUnitId,
        String questionnaireId,
        ArrayNode personalization) {
}
