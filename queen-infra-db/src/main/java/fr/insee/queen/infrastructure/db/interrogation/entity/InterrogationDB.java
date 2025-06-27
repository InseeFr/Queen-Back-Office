package fr.insee.queen.infrastructure.db.interrogation.entity;

import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * campaign of the interrogation
     */
    @ManyToOne
    private CampaignDB campaign;

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

    @OneToOne(mappedBy = "interrogation", cascade = CascadeType.ALL)
    private CommentDB comment;

    public InterrogationDB(String id, String surveyUnitId, CampaignDB campaign, QuestionnaireModelDB questionnaireModel) {
        this.id = id;
        this.surveyUnitId = surveyUnitId;
        this.campaign = campaign;
        this.questionnaireModel = questionnaireModel;
    }
}
