package fr.insee.queen.infrastructure.mongo.surveyunit.document;

import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.surveyunit.model.*;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.CampaignObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

/**
 * Survey unit entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(value="survey-unit")
public class SurveyUnitDocument {
    /**
     * survey unit id
     */
    @Id
    private String id;

    /**
     * questionnaire model of the survey unit
     */
    @Field("questionnaire-id")
    private String questionnaireId;

    @Field("campaign-id")
    private String campaignId;

    @Field("state-data")
    private StateDataObject stateData;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private PersonalizationObject personalization;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private DataObject data;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private CommentObject comment;

    public static SurveyUnit toModel(SurveyUnitDocument surveyUnitDocument) {
        StateData stateData = null;
        if(surveyUnitDocument.getStateData() != null) {
            stateData = StateDataObject.toModel(surveyUnitDocument.getStateData());
        }

        return new SurveyUnit(surveyUnitDocument.getId(),
                surveyUnitDocument.getCampaignId(),
                surveyUnitDocument.getQuestionnaireId(),
                PersonalizationObject.toModel(surveyUnitDocument.getPersonalization()),
                DataObject.toModel(surveyUnitDocument.getData()),
                CommentObject.toModel(surveyUnitDocument.getComment()),
                stateData);
    }

    public static SurveyUnitSummary toSummaryModel(SurveyUnitDocument surveyUnitDocument) {
        return new SurveyUnitSummary(surveyUnitDocument.getId(),
                surveyUnitDocument.getQuestionnaireId(),
                surveyUnitDocument.getCampaignId());
    }

    public static SurveyUnitState toStateModel(SurveyUnitDocument surveyUnitDocument) {
        StateData stateData = null;
        if(surveyUnitDocument.getStateData() != null) {
            stateData = StateDataObject.toModel(surveyUnitDocument.getStateData());
        }
        return new SurveyUnitState(surveyUnitDocument.getId(),
                surveyUnitDocument.getQuestionnaireId(),
                surveyUnitDocument.getCampaignId(),
                stateData);
    }

    public static SurveyUnitDepositProof toDepositProofModel(SurveyUnitDocument surveyUnitDocument, CampaignObject campaignObject) {
        return new SurveyUnitDepositProof(surveyUnitDocument.getId(),
                CampaignObject.toModel(campaignObject),
                StateDataObject.toModel(surveyUnitDocument.getStateData()));
    }

    public static SurveyUnitDocument fromModel(SurveyUnit surveyUnit) {
        StateDataObject stateData = null;
        if(surveyUnit.stateData() != null) {
            stateData = StateDataObject.fromModel(surveyUnit.stateData());
        }

        return new SurveyUnitDocument(surveyUnit.id(),
                surveyUnit.questionnaireId(),
                surveyUnit.campaignId(),
                stateData,
                PersonalizationObject.fromModel(surveyUnit.personalization()),
                DataObject.fromModel(surveyUnit.data()),
                CommentObject.fromModel(surveyUnit.comment()));
    }
}
