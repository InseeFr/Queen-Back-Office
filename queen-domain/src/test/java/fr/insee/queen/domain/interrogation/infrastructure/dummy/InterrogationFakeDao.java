package fr.insee.queen.domain.interrogation.infrastructure.dummy;

import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterrogationFakeDao implements InterrogationRepository {

    @Getter
    private Interrogation interrogationCreated = null;

    @Getter
    private Interrogation interrogationUpdated = null;

    @Setter
    private InterrogationPersonalization interrogationPersonalization;

    @Setter
    private List<InterrogationSummary> interrogationSummaries = new ArrayList<>();

    @Setter
    private boolean interrogationExist = true;

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByGroupId(String groupId) {
        return null;
    }

    @Override
    public List<InterrogationSummary> findAllSummaryBySurveyUnitId(String surveyUnitId) {
        return interrogationSummaries.stream().filter(
                i -> i.surveyUnitId().equals(surveyUnitId)).toList();
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds) {
            return interrogationSummaries
                    .stream()
                    .filter(summary -> interrogationIds.contains(summary.id()))
                    .toList();
    }

    @Override
    public Optional<Interrogation> find(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public Optional<InterrogationDepositProof> findWithGroupAndStateById(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return Optional.empty();
    }

    @Override
    public List<InterrogationState> findAllByState(String groupId, StateDataType stateDataType) {
        return null;
    }

    @Override
    public List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds) {
        return null;
    }

    @Override
    public void deleteInterrogations(String groupId) {
        // not used at this moment for unit tests
    }

    @Override
    public void delete(String interrogationId) {
        // not used at this moment for unit tests
    }

    @Override
    public void create(Interrogation interrogation) {
        interrogationCreated = interrogation;
    }

    @Override
    public void savePersonalization(String interrogationId, ArrayNode personalization) {
        // not used at this moment for unit tests
    }

    @Override
    public void saveData(String interrogationId, ObjectNode data) {
        // not used at this moment for unit tests
    }

    @Override
    public void updateCollectedData(String interrogationId, ObjectNode partialCollectedDataNode) {
        // not used at this moment for unit tests
    }

    @Override
    public Optional<ObjectNode> findData(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public InterrogationPersonalization getInterrogationPersonalization(String interrogationId) {
        return interrogationPersonalization;
    }

    @Override
    public Optional<ArrayNode> findPersonalization(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String interrogationId) {
        return interrogationExist;
    }

    @Override
    public void update(Interrogation interrogation) {
        interrogationUpdated = interrogation;
    }

    @Override
    public List<Interrogation> find(List<String> interrogationIds) {
        return null;
    }

    @Override
    public List<Interrogation> findAll() {
        return null;
    }

    @Override
    public void cleanExtractedData(String groupId, Long startTimestamp, Long endTimestamp) {
        // not used at this moment
    }

    @Getter
    private String cleanedGroupId;

    @Getter
    private List<String> cleanedInterrogationIds;

    @Override
    public void cleanExtractedDataByIds(String groupId, List<String> interrogationIds) {
        this.cleanedGroupId = groupId;
        this.cleanedInterrogationIds = interrogationIds;
    }

    @Override
    public boolean existsByGroupId(String groupId) {
        return false;
    }
}
