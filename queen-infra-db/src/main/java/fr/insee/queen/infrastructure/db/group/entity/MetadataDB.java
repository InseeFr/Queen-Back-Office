package fr.insee.queen.infrastructure.db.group.entity;

import tools.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "metadata")
@Getter
@Setter
@NoArgsConstructor
public class MetadataDB {

    /**
     * The id of the Metadata
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

    /**
     * The group linked to the Metadata
     */
    @OneToOne
    @JoinColumn(name = "survey_group_id")
    private GroupDB group;

    public MetadataDB(ObjectNode value, GroupDB group) {
        this.value = value;
        this.group = group;
    }
}