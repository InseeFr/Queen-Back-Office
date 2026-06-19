package fr.insee.queen.application.interrogation.service.dummy;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class InterrogationFakeService implements InterrogationService {

    private final StateDataFakeService stateDataFakeService;
    public static final String INTERROGATION1_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
    public static final String INTERROGATION2_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02";
    public static final String INTERROGATION3_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa03";
    public static final String INTERROGATION4_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa04";
    public static final String INTERROGATION5_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa05";
    public static final String INTERROGATION6_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa06";

    @Getter
    private final List<Interrogation> interrogations;
    @Getter
    private final List<InterrogationSummary> interrogationSummaries;
    @Setter
    private boolean interrogationExist = true;
    @Getter
    private boolean checkInterrogationExist = false;
    @Getter
    private boolean checkInterrogationNotExist = false;
    @Getter
    private boolean checkInterrogationUpdate = false;
    @Getter
    private boolean checkInterrogationCreated = false;
    @Getter
    private boolean checkInterrogationDeleted = false;
    @Getter
    private Interrogation interrogationUpdated;

    public InterrogationFakeService() {
        stateDataFakeService = new StateDataFakeService();

        CampaignSummary normalCampaign = new CampaignSummary("campaign-id", "campaign-label");
        interrogationSummaries = List.of(
                new InterrogationSummary(INTERROGATION1_ID, "survey-unit-id1", "questionnaire-id", normalCampaign),
                new InterrogationSummary(INTERROGATION2_ID, "survey-unit-id2", "questionnaire-id", normalCampaign)
        );

        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", "data-value");

        interrogations = List.of(
                new Interrogation(INTERROGATION1_ID, "survey-unit-id1", normalCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        stateDataFakeService.getStateData(INTERROGATION1_ID), null),
                new Interrogation(INTERROGATION2_ID, "survey-unit-id2", normalCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        stateDataFakeService.getStateData(INTERROGATION2_ID), null)
        );
    }

    @Override
    public void throwExceptionIfInterrogationNotExist(String interrogationId) {
        checkInterrogationExist = true;
    }

    @Override
    public void throwExceptionIfInterrogationExist(String interrogationId) {
        checkInterrogationNotExist = true;
    }

    @Override
    public boolean existsById(String interrogationId) {
        return interrogationExist;
    }

    @Override
    public Interrogation getInterrogation(String id) {
        return interrogations
                .stream()
                .filter(interrogation -> interrogation.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("interrogation not found"));
    }

    @Override
    public List<InterrogationSummary> findSummariesByCampaignId(String campaignId) {
        return interrogationSummaries;
    }

    @Override
    public List<InterrogationSummary> findSummariesBySurveyUnitId(String surveyUnitId) {
        return interrogationSummaries.stream().filter(interrogation ->
                interrogation.surveyUnitId().equals(surveyUnitId)).toList();
    }

    @Override
    public List<String> findAllInterrogationIds() {
        return null;
    }

    @Override
    public void updateInterrogation(Interrogation interrogation) {
        interrogationUpdated = interrogation;
        checkInterrogationUpdate = true;
    }

    @Override
    public void updateInterrogation(String interrogationId, ObjectNode data, StateData stateData) {
        checkInterrogationUpdate = true;
        interrogationUpdated = new Interrogation(interrogationId, "survey-unit-id", null, null, null, data, stateData, null);
    }

    @Override
    public void createInterrogation(Interrogation interrogation) {
        checkInterrogationCreated = true;
    }

    @Override
    public List<InterrogationSummary> findSummariesByIds(List<String> interrogationIds) {
        return interrogationSummaries
                .stream()
                .filter(interrogationSummary -> interrogationIds.contains(interrogationSummary.id()))
                .toList();
    }

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        return interrogationSummaries
                .stream()
                .filter(interrogationSummary -> interrogationSummary.id().equals(interrogationId))
                .findFirst();
    }

    @Override
    public List<InterrogationState> findWithStateByIds(List<String> interrogations) {
        return null;
    }

    @Override
    public void delete(String interrogationId) {
        checkInterrogationDeleted = true;
    }

    @Override
    public InterrogationDepositProof getInterrogationDepositProof(String interrogationId) {
        return null;
    }

    @Override
    public InterrogationMetadata getInterrogationMetadata(String interrogationId) {
        return null;
    }

    @Override
    public InterrogationSummary getSummaryById(String interrogationId) {
        return interrogationSummaries
                .stream()
                .filter(interrogationSummary -> interrogationSummary.id().equals(interrogationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("interrogation not found"));
    }

    @Override
    public List<Interrogation> findByIds(List<String> interrogationIds) {
        return null;
    }

    @Override
    public List<Interrogation> findAllInterrogations() {
        return List.of(
                new Interrogation(INTERROGATION1_ID, "survey-unit-id1", "campaign-id", "questionnaire-id", null, null, null, null),
                new Interrogation(INTERROGATION2_ID, "survey-unit-id2", "campaign-id", "questionnaire-id", null, null, null, null)
        );
    }

    @Override
    public List<InterrogationState> getInterrogations(String campaignId, StateDataType stateDataType) {
        return null;
    }
}
