package fr.insee.queen.application.pilotage.controller.dummy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PilotageFakeComponent implements PilotageComponent {
    @Getter
    private boolean checked = false;
    @Setter
    private boolean isCampaignClosed = true;
    @Getter
    private boolean wentThroughInterviewerCampaigns = false;
    @Setter
    private boolean hasEmptySurveyUnits = false;

    public static final String CAMPAIGN1_ID = "interviewerCampaign1";
    public static final String SURVEY_UNIT1_ID = "pilotage-component-s1";
    public static final String SURVEY_UNIT2_ID = "pilotage-component-s2";

    @Override
    public void checkHabilitations(String surveyUnitId, PilotageRole... roles) {
        checked = true;
    }

    @Override
    public boolean isClosed(String campaignId) {
        return this.isCampaignClosed;
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId) {
        if (this.hasEmptySurveyUnits) {
            return new ArrayList<>();
        }
        return List.of(
                new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", "campaign-id"),
                new SurveyUnitSummary("s2", "questionnaire-id", "campaign-id")
        );
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        wentThroughInterviewerCampaigns = true;
        return List.of(
                new PilotageCampaign(CAMPAIGN1_ID, new ArrayList<>()),
                new PilotageCampaign("interviewerCampaign2", new ArrayList<>())
        );
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
}
