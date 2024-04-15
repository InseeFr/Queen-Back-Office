package fr.insee.queen.application.pilotage.service.dummy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.PilotageService;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class PilotageFakeService implements PilotageService {

    @Setter
    private boolean isCampaignClosed = true;
    @Setter
    private boolean hasHabilitation = true;
    @Getter
    private boolean wentThroughInterviewerCampaigns = false;
    @Getter
    private int wentThroughHasHabilitation = 0;
    @Setter
    private boolean hasEmptySurveyUnits = false;

    public static final String CAMPAIGN1_ID = "interviewerCampaign1";
    public static final String SURVEY_UNIT1_ID = "pilotage-s1";
    public static final String SURVEY_UNIT2_ID = "pilotage-s2";

    @Getter
    private final List<SurveyUnitSummary> surveyUnitSummaries = List.of(
            new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", "campaign-id"),
            new SurveyUnitSummary("s2", "questionnaire-id", "campaign-id")
    );

    @Getter
    private final List<PilotageCampaign> campaignsForInterviewer = List.of(
            new PilotageCampaign(CAMPAIGN1_ID, new ArrayList<>()),
            new PilotageCampaign("interviewerCampaign2", new ArrayList<>())
    );

    @Override
    public boolean isClosed(String campaignId) {
        return this.isCampaignClosed;
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId) {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return surveyUnitSummaries;
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits() {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return List.of(
                new SurveyUnit(SURVEY_UNIT1_ID, "campaign-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3")),
                new SurveyUnit(SURVEY_UNIT2_ID, "campaign-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3"))
        );
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        wentThroughInterviewerCampaigns = true;
        return campaignsForInterviewer;
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep) {
        wentThroughHasHabilitation++;
        return this.hasHabilitation;
    }
}
