package fr.insee.queen.infrastructure.db.surveyunit.data.entity.unciphered;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.SurveyUnitDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "data")
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("0")
public class UncipheredDataDB extends DataDB {

    /**
     * The value of data (jsonb format)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name="value")
    private ObjectNode value;

    public UncipheredDataDB(ObjectNode value, SurveyUnitDB surveyUnit) {
        this.setSurveyUnit(surveyUnit);
        this.value = value;
    }
}