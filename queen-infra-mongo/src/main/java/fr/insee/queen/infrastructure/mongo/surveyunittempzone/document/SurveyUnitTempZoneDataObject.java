package fr.insee.queen.infrastructure.mongo.surveyunittempzone.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUnitTempZoneDataObject {
    @Field("data")
    private ObjectNode value;

    public static SurveyUnitTempZoneDataObject fromModel(ObjectNode surveyUnit) {
        return new SurveyUnitTempZoneDataObject(surveyUnit);
    }

    public static ObjectNode toModel(SurveyUnitTempZoneDataObject surveyUnitTempZoneDataObject) {
        return surveyUnitTempZoneDataObject.getValue();
    }
}
