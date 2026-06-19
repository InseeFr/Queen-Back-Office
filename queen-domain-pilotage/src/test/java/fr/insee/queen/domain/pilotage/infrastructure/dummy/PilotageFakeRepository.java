package fr.insee.queen.domain.pilotage.infrastructure.dummy;

import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PermissionEnum;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PilotageFakeRepository implements PilotageRepository {

    public static final String INTERVIEWER_GROUP1_ID = "interviewer-group1";
    public static final String CURRENT_SU_GROUP1_ID = "group-id";

    public static final String INTERROGATION1_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
    public static final String INTERROGATION2_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02";
    public static final String INTERROGATION3_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa03";

    @Getter
    private boolean wentThroughIsClosedGroup = false;
    @Getter
    private boolean wentThroughHasHabilitation = true;
    @Setter
    private boolean nullInterviewerGroups = false;
    @Setter
    private boolean nullCurrentInterrogation = false;

    @Override
    public boolean isClosed(String groupId) {
        wentThroughIsClosedGroup = true;
        return false;
    }

    @Override
    public List<PilotageInterrogation> getInterrogations() {
        if (nullCurrentInterrogation) {
            return null;
        }
        return List.of(
                new PilotageInterrogation(INTERROGATION1_ID, CURRENT_SU_GROUP1_ID),
                new PilotageInterrogation(INTERROGATION2_ID, "group-id2"),
                new PilotageInterrogation(INTERROGATION3_ID, CURRENT_SU_GROUP1_ID)
        );
    }

    @Override
    public List<PilotageGroup> getInterviewerGroups() {
        if (nullInterviewerGroups) {
            return null;
        }
        return List.of(
                new PilotageGroup(INTERVIEWER_GROUP1_ID, List.of("questionnaire-id")),
                new PilotageGroup("interviewer-group2", List.of("questionnaire-id"))
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
