package fr.insee.queen.infrastructure.db.interrogation.projection;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.Interrogation;

public record InterrogationProjection(
        String id,
        String surveyUnitId,
        String campaignId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateDataType state,
        Long date,
        String currentPage,
        Boolean locked) {

    public static Interrogation toModel(InterrogationProjection projection) {
        StateData stateDataModel = null;
        if(projection.state != null || projection.date() != null || projection.currentPage() != null) {
            stateDataModel = new StateData(projection.state(), projection.date(), projection.currentPage());
        }
        return new Interrogation(projection.id(),
                projection.surveyUnitId(),
                projection.campaignId(),
                projection.questionnaireId(),
                projection.personalization(),
                projection.data(),
                projection.comment(),
                stateDataModel,
                null,
                projection.locked());
    }
}
