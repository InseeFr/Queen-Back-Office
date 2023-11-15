package fr.insee.queen.api.campaign.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

/**
 * Questionnaire entity
 */
@Entity
@Table(name = "questionnaire_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireModelDB {
    /**
     * questionnaire id
     */
    @Id
    @Column(length = 50)
    private String id;

    /**
     * questionnaire label
     */
    @Column(nullable = false)
    private String label;

    /**
     * the data structure of the questionnaire (json format)
     */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String value;

    /**
     * required nomenclatures for the questionnaire
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "required_nomenclature",
            joinColumns = {@JoinColumn(name = "id_required_nomenclature")}, inverseJoinColumns = {@JoinColumn(name = "code")})
    private Set<NomenclatureDB> nomenclatures = new HashSet<>();

    /**
     * The campaign linked to the questionnaire
     */
    @ManyToOne
    private CampaignDB campaign;

    public QuestionnaireModelDB(String id, String label, String value, Set<NomenclatureDB> nomenclatures) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.nomenclatures = nomenclatures;
    }
}
