package fr.insee.queen.domain.surveyunit.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public record SurveyUnitMetadata(
        SurveyUnitPersonalization surveyUnitPersonalization,
        ObjectNode metadata) {

    public static SurveyUnitMetadata create(SurveyUnitPersonalization surveyUnitPersonalization, ObjectNode metadata) {
        return new SurveyUnitMetadata(surveyUnitPersonalization, metadata);
    }
}
