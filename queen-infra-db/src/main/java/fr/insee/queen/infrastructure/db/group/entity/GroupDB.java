package fr.insee.queen.infrastructure.db.group.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Group Entity
 *
 */
@Entity
@Table(name = "survey_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class    GroupDB {
    /**
     * The group id
     */
    @Id
    @Column(length = 50)
    private String id;

    /**
     * The group label
     */
    @Column(nullable = false)
    private String label;

    @Column(name = "kind", nullable = false, length = 20)
    private String kind;

    @Column(name = "short_label")
    private String shortLabel;

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private MetadataDB metadata;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "survey_group_questionnaire_model",
            joinColumns = @JoinColumn(name = "survey_group_id"),
            inverseJoinColumns = @JoinColumn(name = "questionnaire_model_id"))
    private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();

    public GroupDB(String id, String label, String shortLabel, Set<QuestionnaireModelDB> questionnaireModels, String kind) {
        this.id = id;
        this.label = label;
        this.shortLabel = shortLabel;
        this.questionnaireModels = questionnaireModels;
        this.kind = kind;
    }
}
