package fr.insee.queen.infrastructure.db.surveyunit.data.entity.ciphered;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.SurveyUnitDB;
import fr.insee.queen.infrastructure.db.surveyunit.repository.converter.ObjectNodeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("1")
public class CipheredDataDB extends DataDB {
    /**
     * The value of data (jsonb format)
     */
    @Column(columnDefinition = "bytea")
    @Convert(converter = ObjectNodeConverter.class)
    @ColumnTransformer(
            read  = "pgp_sym_decrypt(value, current_setting('data.encryption.key'))",
            write = "pgp_sym_encrypt(?, current_setting('data.encryption.key'))"
    )
    private ObjectNode value;

    public CipheredDataDB(ObjectNode value, SurveyUnitDB surveyUnit) {
        this.setSurveyUnit(surveyUnit);
        this.value = value;
    }
}