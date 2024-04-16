package fr.insee.queen.infrastructure.mongo.surveyunittempzone.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;

/**
 * Entity surveyUnitTempZone
 *
 * @author Laurent Caouissin
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUnitTempZoneDocument {

    /**
     * The unique id of surveyUnitTempZone
     */
    @Id
    private UUID id;

    /**
     * The id of surveyUnit
     */
    @Field("survey-unit-id")
    private String surveyUnitId;

    /**
     * The id of user
     */
    @Field("user-id")
    private String userId;

    /**
     * The date of save
     */
    @Field("date")
    private Long date;
    /**
     * The value of surveyUnit (jsonb format)
     */
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private SurveyUnitTempZoneDataObject surveyUnit;

    public SurveyUnitTempZoneDocument(String surveyUnitId, String userId, Long date, SurveyUnitTempZoneDataObject surveyUnit) {
        this.surveyUnitId = surveyUnitId;
        this.userId = userId;
        this.date = date;
        this.surveyUnit = surveyUnit;
    }

    public static SurveyUnitTempZoneDocument fromModel(String surveyUnitId, String userId, Long date, ObjectNode surveyUnit) {
        return new SurveyUnitTempZoneDocument(surveyUnitId, userId, date, SurveyUnitTempZoneDataObject.fromModel(surveyUnit));
    }

    public static SurveyUnitTempZone toModel(SurveyUnitTempZoneDocument surveyUnitTempZoneDocument) {
        return new SurveyUnitTempZone(surveyUnitTempZoneDocument.getId(),
                surveyUnitTempZoneDocument.getSurveyUnitId(),
                surveyUnitTempZoneDocument.getUserId(),
                surveyUnitTempZoneDocument.getDate(),
                SurveyUnitTempZoneDataObject.toModel(surveyUnitTempZoneDocument.getSurveyUnit()));
    }
}
