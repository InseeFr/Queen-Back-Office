package fr.insee.queen.api.pilotage.service.dummy;

import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.PilotageService;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
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
    private final List<PilotageCampaign> interviewerCampaigns = List.of(
            new PilotageCampaign(CAMPAIGN1_ID, new ArrayList<>()),
            new PilotageCampaign("interviewerCampaign2", new ArrayList<>())
    );

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        return this.isCampaignClosed;
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return surveyUnitSummaries;
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits(String authToken) {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return List.of(
                new SurveyUnit(SURVEY_UNIT1_ID, "campaign-id", "questionnaire-id",
                        "[]", "{}", "{}",
                        new StateData(StateDataType.INIT, 0L, "2#3")),
                new SurveyUnit(SURVEY_UNIT2_ID, "campaign-id", "questionnaire-id",
                        "[]", "{}", "{}",
                        new StateData(StateDataType.INIT, 0L, "2#3"))
        );
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        wentThroughInterviewerCampaigns = true;
        return interviewerCampaigns;
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        wentThroughHasHabilitation++;
        return this.hasHabilitation;
    }
}
