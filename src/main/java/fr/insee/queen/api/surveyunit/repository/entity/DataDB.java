package fr.insee.queen.api.surveyunit.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "data")
@Getter
@Setter
@NoArgsConstructor
public class DataDB {

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
    private String value;

    /**
     * The SurveyUnit associated
     */
    @OneToOne
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
    private SurveyUnitDB surveyUnit;

    public DataDB(String value, SurveyUnitDB surveyUnit) {
        this.value = value;
        this.surveyUnit = surveyUnit;
    }
}