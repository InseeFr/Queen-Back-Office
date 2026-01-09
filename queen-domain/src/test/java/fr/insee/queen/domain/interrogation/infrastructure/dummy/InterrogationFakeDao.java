package fr.insee.queen.domain.interrogation.infrastructure.dummy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public List<InterrogationSummary> findAllSummaryByCampaignId(String campaignId) {
        return null;
    }

    @Override
    public List<InterrogationSummary> findAllSummaryBySurveyUnitId(String surveyUnitId) {
        return interrogationSummaries.stream().filter(
                i -> i.surveyUnitId().equals(surveyUnitId)).toList();
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds) {
        return null;
    }

    @Override
    public Optional<Interrogation> find(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public Optional<InterrogationDepositProof> findWithCampaignAndStateById(String interrogationId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return Optional.empty();
    }

    @Override
    public List<InterrogationState> findAllByState(String campaignId, StateDataType stateDataType) {
        return null;
    }

    @Override
    public List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds) {
        return null;
    }

    @Override
    public void deleteInterrogations(String campaignId) {
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
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        // not used at this moment
    }
}
