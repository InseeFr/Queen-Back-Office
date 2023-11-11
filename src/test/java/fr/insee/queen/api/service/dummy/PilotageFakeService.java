package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.PilotageService;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
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
    public static final String SURVEY_UNIT1_ID = "s1";

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        return this.isCampaignClosed;
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return List.of(
                new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", "campaign-id"),
                new SurveyUnitSummary("s2", "questionnaire-id", "campaign-id")
        );
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        wentThroughInterviewerCampaigns = true;
        return List.of(
                new PilotageCampaign(CAMPAIGN1_ID, new ArrayList<>()),
                new PilotageCampaign("interviewerCampaign2", new ArrayList<>())
        );
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        wentThroughHasHabilitation++;
        return this.hasHabilitation;
    }
}
