package fr.insee.queen.application.pilotage.service.dummy;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.service.PilotageService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class PilotageFakeService implements PilotageService {

    @Setter
    private boolean isGroupClosed = true;
    @Setter
    private boolean hasHabilitation = true;
    @Getter
    private boolean wentThroughInterviewerGroups = false;
    @Setter
    private boolean hasEmptyInterrogations = false;

    public static final String GROUP1_ID = "interviewerGroup1";
    public static final String INTERROGATION1_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
    public static final String INTERROGATION2_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02";

    public final GroupSummary groupSummary = new GroupSummary("group-id", "label");

    @Getter
    private final List<InterrogationSummary> interrogationSummaries = List.of(
            new InterrogationSummary(INTERROGATION1_ID, "survey-unit-id1", "questionnaire-id", groupSummary),
            new InterrogationSummary(INTERROGATION2_ID, "survey-unit-id2", "questionnaire-id", groupSummary)
    );

    @Getter
    private final List<PilotageGroup> groupsForInterviewer = List.of(
            new PilotageGroup(GROUP1_ID, new ArrayList<>()),
            new PilotageGroup("interviewerGroup2", new ArrayList<>())
    );

    @Override
    public boolean isClosed(String groupId) {
        return this.isGroupClosed;
    }

    @Override
    public List<InterrogationSummary> getInterrogations(String groupId) {
        if (this.hasEmptyInterrogations) {
            return new ArrayList<>();
        }
        return interrogationSummaries;
    }

    @Override
    public List<Interrogation> getInterviewerInterrogations() {
        if (this.hasEmptyInterrogations) {
            return new ArrayList<>();
        }
        return List.of(
                new Interrogation(INTERROGATION1_ID, "survey-unit-id1", "group-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3"),
                        null),
                new Interrogation(INTERROGATION2_ID, "survey-unit-id2", "group-id", "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(),
                        JsonNodeFactory.instance.objectNode(),
                        new StateData(StateDataType.INIT, 0L, "2#3"),
                        null)
        );
    }

    @Override
    public List<PilotageGroup> getInterviewerGroups() {
        wentThroughInterviewerGroups = true;
        return groupsForInterviewer;
    }
}
