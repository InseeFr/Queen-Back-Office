package fr.insee.queen.api.pilotage.repository.dummy;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.gateway.PilotageRepository;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

public class PilotageFakeRepository implements PilotageRepository {

    public static final String INTERVIEWER_CAMPAIGN1_ID = "interviewer-campaign1";

    public static final String SURVEY_UNIT1_ID = "pilotage-su-1";
    public static final String SURVEY_UNIT2_ID = "pilotage-su-2";
    public static final String SURVEY_UNIT3_ID = "pilotage-su-2";

    @Getter
    private boolean wentThroughIsClosedCampaign = false;
    @Getter
    private boolean wentThroughHasHabilitation = true;
    @Setter
    private boolean nullInterviewerCampaigns = false;
    @Setter
    private boolean nullSurveyUnits = false;

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        wentThroughIsClosedCampaign = true;
        return false;
    }

    @Override
    public List<LinkedHashMap<String, String>> getSurveyUnits(String authToken, String campaignId) {
        if (nullSurveyUnits) {
            return null;
        }
        LinkedHashMap<String, String> map1 = new LinkedHashMap<>();
        map1.put("campaign", campaignId);
        map1.put("id", SURVEY_UNIT1_ID);
        LinkedHashMap<String, String> map2 = new LinkedHashMap<>();
        map2.put("campaign", "su-campaign2");
        map2.put("id", SURVEY_UNIT2_ID);
        LinkedHashMap<String, String> map3 = new LinkedHashMap<>();
        map3.put("campaign", campaignId);
        map3.put("id", SURVEY_UNIT3_ID);
        return List.of(map1, map2, map3);
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        if (nullInterviewerCampaigns) {
            return null;
        }
        return List.of(
                new PilotageCampaign(INTERVIEWER_CAMPAIGN1_ID, List.of("questionnaire-id")),
                new PilotageCampaign("interviewer-campaign2", List.of("questionnaire-id"))
        );
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        this.wentThroughHasHabilitation = true;
        return true;
    }

}
