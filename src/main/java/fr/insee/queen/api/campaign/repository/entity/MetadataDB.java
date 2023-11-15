package fr.insee.queen.api.campaign.repository.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private String value;

    /**
     * The campaign linked to the Metadata
     */
    @OneToOne
    private CampaignDB campaign;

    public MetadataDB(String value, CampaignDB campaign) {
        this.value = value;
        this.campaign = campaign;
    }
}