package fr.insee.queen.infrastructure.db.surveyunit.entity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "personalization")
@Getter
@Setter
@NoArgsConstructor
public class PersonalizationDB {

    /**
     * The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The value of data (jsonb format)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private ArrayNode value;

    /**
     * The SurveyUnit associated
     */
    @OneToOne
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
    private SurveyUnitDB surveyUnit;

    public PersonalizationDB(ArrayNode value, SurveyUnitDB surveyUnit) {
        this.value = value;
        this.surveyUnit = surveyUnit;
    }
}