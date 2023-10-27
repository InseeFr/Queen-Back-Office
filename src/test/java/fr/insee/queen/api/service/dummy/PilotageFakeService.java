package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.service.pilotage.PilotageService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class PilotageFakeService implements PilotageService {

    @Setter
    private boolean isCampaignClosed = true;
    @Getter
    private boolean wentThroughInterviewerCampaigns = false;
    @Setter
    private boolean hasEmptySurveyUnits = false;

    public static final String CAMPAIGN1_ID = "interviewerCampaign1";
    public static final String SURVEY_UNIT1_ID = "s1";

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        return this.isCampaignClosed;
    }

    @Override
    public List<SurveyUnitSummaryDto> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        if(hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return List.of(
                new SurveyUnitSummaryDto(SURVEY_UNIT1_ID, "questionnaire-id"),
                new SurveyUnitSummaryDto("s2", "questionnaire-id")
        );
    }

    @Override
    public List<CampaignSummaryDto> getInterviewerCampaigns(String authToken) {
        wentThroughInterviewerCampaigns = true;
        return List.of(
                new CampaignSummaryDto(CAMPAIGN1_ID, new ArrayList<>()),
                new CampaignSummaryDto("interviewerCampaign2", new ArrayList<>())
        );
    }

    @Override
    public boolean hasHabilitation(SurveyUnitHabilitationDto surveyUnit, String role, String idep, String authToken) {
        return false;
    }
}
