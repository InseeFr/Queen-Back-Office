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
public class GroupDB {
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

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private MetadataDB metadata;


    @OneToMany(fetch = FetchType.LAZY, targetEntity = QuestionnaireModelDB.class, cascade = CascadeType.ALL, mappedBy = "group")
    private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();

    public GroupDB(String id, String label, Set<QuestionnaireModelDB> questionnaireModels) {
        super();
        this.id = id;
        this.label = label;
        this.questionnaireModels = questionnaireModels;
    }
}
