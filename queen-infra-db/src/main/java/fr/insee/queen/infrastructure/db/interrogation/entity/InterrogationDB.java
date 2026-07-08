package fr.insee.queen.infrastructure.db.interrogation.entity;

import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Interrogation entity
 */
@Entity
@Table(name = "interrogation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterrogationDB {
    /**
     * interrogation id
     */
    @Id
    private String id;

    @Column(name = "survey_unit_id")
    @NotNull
    private String surveyUnitId;

    /**
     * group of the interrogation
     */
    @ManyToOne
    @JoinColumn(name = "survey_group_id")
    private GroupDB group;

    /**
     * questionnaire model of the interrogation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_model_id", referencedColumnName = "id")
    private QuestionnaireModelDB questionnaireModel;

    @OneToOne(mappedBy = "interrogation", cascade = CascadeType.ALL)
    private StateDataDB stateData;

    @OneToOne(mappedBy = "interrogation", cascade = CascadeType.ALL)
    private PersonalizationDB personalization;

    @OneToOne(mappedBy = "interrogation", cascade = CascadeType.ALL)
    private DataDB data;

    @Column(name = "correlation_id")
    private UUID correlationId;

    public InterrogationDB(String id, String surveyUnitId, GroupDB group, QuestionnaireModelDB questionnaireModel, UUID correlationId) {
        this.id = id;
        this.surveyUnitId = surveyUnitId;
        this.group = group;
        this.questionnaireModel = questionnaireModel;
        this.correlationId = correlationId;
    }
}
