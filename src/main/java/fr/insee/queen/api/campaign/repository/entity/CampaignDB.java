package fr.insee.queen.api.campaign.repository.entity;

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
    @org.springframework.data.annotation.Id
    @Column(length = 50)
    private String id;

    /**
     * The campaign label
     */
    @Column(nullable = false)
    private String label;


    @OneToOne(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private MetadataDB metadata;


    @OneToMany(fetch = FetchType.LAZY, targetEntity = QuestionnaireModelDB.class, cascade = CascadeType.ALL, mappedBy = "campaign")
    private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();

    public CampaignDB(String id, String label, Set<QuestionnaireModelDB> questionnaireModels) {
        super();
        this.id = id;
        this.label = label;
        this.questionnaireModels = questionnaireModels;
    }
}
