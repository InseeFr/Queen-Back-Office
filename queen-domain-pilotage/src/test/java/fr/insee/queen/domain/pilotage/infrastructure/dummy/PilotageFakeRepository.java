package fr.insee.queen.domain.pilotage.infrastructure.dummy;

import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PermissionEnum;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PilotageFakeRepository implements PilotageRepository {

    public static final String INTERVIEWER_CAMPAIGN1_ID = "interviewer-campaign1";
    public static final String CURRENT_SU_CAMPAIGN1_ID = "campaign-id";

    public static final String INTERROGATION1_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
    public static final String INTERROGATION2_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02";
    public static final String INTERROGATION3_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa03";

    @Getter
    private boolean wentThroughIsClosedCampaign = false;
    @Getter
    private boolean wentThroughHasHabilitation = true;
    @Setter
    private boolean nullInterviewerCampaigns = false;
    @Setter
    private boolean nullCurrentInterrogation = false;

    @Override
    public boolean isClosed(String campaignId) {
        wentThroughIsClosedCampaign = true;
        return false;
    }

    @Override
    public List<PilotageInterrogation> getInterrogations() {
        if (nullCurrentInterrogation) {
            return null;
        }
        return List.of(
                new PilotageInterrogation(INTERROGATION1_ID, CURRENT_SU_CAMPAIGN1_ID),
                new PilotageInterrogation(INTERROGATION2_ID, "campaign-id2"),
                new PilotageInterrogation(INTERROGATION3_ID, CURRENT_SU_CAMPAIGN1_ID)
        );
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        if (nullInterviewerCampaigns) {
            return null;
        }
        return List.of(
                new PilotageCampaign(INTERVIEWER_CAMPAIGN1_ID, List.of("questionnaire-id")),
                new PilotageCampaign("interviewer-campaign2", List.of("questionnaire-id"))
        );
    }

    @Override
    public boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        return false;
    }

    @Override
    public boolean hasPermission(InterrogationSummary interrogation, PermissionEnum permission) {
        return false;
    }

}
