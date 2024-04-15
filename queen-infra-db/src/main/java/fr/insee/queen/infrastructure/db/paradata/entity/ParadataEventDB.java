package fr.insee.queen.infrastructure.db.paradata.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.SurveyUnitDB;
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
    private ObjectNode value;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnitDB surveyUnit;
}
