package fr.insee.queen.infrastructure.db.surveyunit.projection;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;

public record SurveyUnitProjection(
        String id,
        String campaignId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateDataType state,
        Long date,
        String currentPage) {

    public static SurveyUnit toModel(SurveyUnitProjection projection) {
        StateData stateDataModel = null;
        if(projection.state != null || projection.date() != null || projection.currentPage() != null) {
            stateDataModel = new StateData(projection.state(), projection.date(), projection.currentPage());
        }
        return new SurveyUnit(projection.id(),
                projection.campaignId(),
                projection.questionnaireId(),
                projection.personalization(),
                projection.data(),
                projection.comment(),
                stateDataModel);
    }
}
