package fr.insee.queen.domain.interrogation.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterrogationFakeService implements InterrogationService {

    public static final String INTERROGATION1_ID = "interrogation1";
    public static final String INTERROGATION2_ID = "interrogation2";

    @Setter
    private boolean interrogationExist = true;
    @Getter
    private boolean checkInterrogationExist = false;

    @Getter
    private boolean checkInterrogationNotExist = false;

    @Getter
    private final List<InterrogationSummary> interrogationSummaries = List.of(
            new InterrogationSummary(INTERROGATION1_ID, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL)),
            new InterrogationSummary("interrogation2", "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL)),
            new InterrogationSummary("interrogation3", "questionnaire-id", new CampaignSummary("campaign-id2", "campaign-label", CampaignSensitivity.SENSITIVE))
    );

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
        return null;
    }

    @Override
    public List<InterrogationSummary> findSummariesByCampaignId(String campaignId) {
        return interrogationSummaries;
    }

    @Override
    public List<String> findAllInterrogationIds() {
        return null;
    }

    @Override
    public void updateInterrogation(Interrogation interrogation) {
        // not used for test at this moment
    }

    @Override
    public void updateInterrogation(String interrogationId, ObjectNode data, StateData stateData) {
        // not used for test at this moment
    }

    @Override
    public void createInterrogation(Interrogation interrogation) {
        // not used for test at this moment
    }

    @Override
    public List<InterrogationSummary> findSummariesByIds(List<String> interrogationIds) {
        List<InterrogationSummary> interrogations = new ArrayList<>();

        interrogationIds.forEach(interrogationId -> interrogations.add(new InterrogationSummary(interrogationId, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL))));
        return interrogations;
    }

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        InterrogationSummary interrogation = new InterrogationSummary(interrogationId, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
        return Optional.of(interrogation);
    }

    @Override
    public List<InterrogationState> findWithStateByIds(List<String> interrogations) {
        return null;
    }

    @Override
    public void delete(String interrogationId) {
        // not used for test at this moment
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
        return new InterrogationSummary(INTERROGATION1_ID, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
    }


    @Override
    public List<Interrogation> findByIds(List<String> interrogationIds) {
        return null;
    }

    @Override
    public List<Interrogation> findAllInterrogations() {
        return List.of(
                new Interrogation(INTERROGATION1_ID, "campaign-id", "questionnaire-id", null, null, null, null),
                new Interrogation(INTERROGATION2_ID, "campaign-id", "questionnaire-id", null, null, null, null)
        );
    }

    @Override
    public List<InterrogationState> getInterrogations(String campaignId, StateDataType stateDataType) {
        return null;
    }
}
