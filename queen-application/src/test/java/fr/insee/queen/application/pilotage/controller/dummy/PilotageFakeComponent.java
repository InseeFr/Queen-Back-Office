package fr.insee.queen.application.pilotage.controller.dummy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
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
    private boolean hasEmptyInterrogations = false;

    public static final String CAMPAIGN1_ID = "interviewerCampaign1";
    public static final String INTERROGATION1_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
    public static final String INTERROGATION2_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02";

    @Override
    public void checkHabilitations(String interrogationId, PilotageRole... roles) {
        checked = true;
    }

    @Override
    public boolean isClosed(String campaignId) {
        return this.isCampaignClosed;
    }

    @Override
    public List<InterrogationSummary> getInterrogationsByCampaign(String campaignId) {
        if (this.hasEmptyInterrogations) {
            return new ArrayList<>();
        }
        CampaignSummary campaignSummary = new CampaignSummary("campaign-id", "label", CampaignSensitivity.NORMAL);
        return List.of(
                new InterrogationSummary(INTERROGATION1_ID, "survey-unit-id1", "questionnaire-id", campaignSummary),
                new InterrogationSummary(INTERROGATION2_ID, "survey-unit-id2", "questionnaire-id", campaignSummary)
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
    public List<Interrogation> getInterviewerInterrogations() {
        if (this.hasEmptyInterrogations) {
            return new ArrayList<>();
        }
        return List.of(
                new Interrogation(INTERROGATION1_ID, "survey-unit-id1", "campaign-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3"),
                        null),
                new Interrogation(INTERROGATION2_ID, "survey-unit-id2", "campaign-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3"),
                        null)
        );
    }
}
