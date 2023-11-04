package fr.insee.queen.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="metadata")
@Getter
@Setter
@AllArgsConstructor
public class MetadataDB {

    /**
     * The id of the Metadata
     */
    @Id
    @org.springframework.data.annotation.Id
    private UUID id;

    /**
     * The value of data (jsonb format)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String value;

    /**
     * The campaign associated to the Metadata
     */
    @OneToOne
    private CampaignDB campaign;

    public MetadataDB() {
        super();
        this.id = UUID.randomUUID();
    }
}