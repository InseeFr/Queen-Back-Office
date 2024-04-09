package fr.insee.queen.infrastructure.db.surveyunittempzone.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Entity surveyUnitTempZone
 *
 * @author Laurent Caouissin
 */
@Entity
@Table(name = "survey_unit_temp_zone")
@Getter
@Setter
@NoArgsConstructor
public class SurveyUnitTempZoneDB {

    /**
     * The unique id of surveyUnitTempZone
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The id of surveyUnit
     */
    @Column(name = "survey_unit_id")
    private String surveyUnitId;

    /**
     * The id of user
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * The date of save
     */
    @Column
    private Long date;
    /**
     * The value of surveyUnit (jsonb format)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private ObjectNode surveyUnit;

    public SurveyUnitTempZoneDB(String surveyUnitId, String userId, Long date, ObjectNode surveyUnit) {
        this.surveyUnitId = surveyUnitId;
        this.userId = userId;
        this.date = date;
        this.surveyUnit = surveyUnit;
    }
}
