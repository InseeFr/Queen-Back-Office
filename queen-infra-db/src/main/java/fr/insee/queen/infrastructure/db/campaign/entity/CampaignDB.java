package fr.insee.queen.infrastructure.db.campaign.entity;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Campaign Entity
 *
 */
@Entity
@Table(name = "campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDB {
    /**
     * The campaign id
     */
    @Id
    @Column(length = 50)
    private String id;

    /**
     * The campaign label
     */
    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CampaignSensitivity sensitivity;

    @OneToOne(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private MetadataDB metadata;


    @OneToMany(fetch = FetchType.LAZY, targetEntity = QuestionnaireModelDB.class, cascade = CascadeType.ALL, mappedBy = "campaign")
    private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();

    public CampaignDB(String id, String label, CampaignSensitivity sensitivity, Set<QuestionnaireModelDB> questionnaireModels) {
        super();
        this.id = id;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireModels = questionnaireModels;
    }
}
