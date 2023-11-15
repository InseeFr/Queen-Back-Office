package fr.insee.queen.api.paradata.repository.entity;

import fr.insee.queen.api.surveyunit.repository.entity.SurveyUnitDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "paradata_event")
@Getter
@Setter
@NoArgsConstructor
public class ParadataEventDB {

    /**
     * The id of the ParadataEvent
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

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnitDB surveyUnit;
}
