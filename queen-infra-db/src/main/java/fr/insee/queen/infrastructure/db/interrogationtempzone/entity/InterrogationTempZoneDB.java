package fr.insee.queen.infrastructure.db.interrogationtempzone.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Entity interrogationTempZone
 *
 * @author Laurent Caouissin
 */
@Entity
@Table(name = "interrogation_temp_zone")
@Getter
@Setter
@NoArgsConstructor
public class InterrogationTempZoneDB {

    /**
     * The unique id of interrogationTempZone
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The id of interrogation
     */
    @Column(name = "interrogation_id")
    private String interrogationId;

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
     * The value of interrogation (jsonb format)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private ObjectNode interrogation;

    public InterrogationTempZoneDB(String interrogationId, String userId, Long date, ObjectNode interrogation) {
        this.interrogationId = interrogationId;
        this.userId = userId;
        this.date = date;
        this.interrogation = interrogation;
    }
}
