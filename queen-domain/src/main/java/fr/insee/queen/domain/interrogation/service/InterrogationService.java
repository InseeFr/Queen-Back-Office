package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;

import java.util.List;
import java.util.Optional;

public interface InterrogationService {
    boolean existsById(String interrogationId);

    void throwExceptionIfInterrogationNotExist(String interrogationId);

    void throwExceptionIfInterrogationExist(String interrogationId);

    Interrogation getInterrogation(String id);

    List<InterrogationSummary> findSummariesByCampaignId(String campaignId);

    List<InterrogationSummary> findSummariesBySurveyUnitId(String surveyUnitId);

    List<String> findAllInterrogationIds();

    void updateInterrogation(Interrogation interrogation);

    void updateInterrogation(String interrogationId, ObjectNode data, StateData stateData);

    void createInterrogation(Interrogation interrogation) throws StateDataInvalidDateException;

    List<InterrogationSummary> findSummariesByIds(List<String> interrogations);

    Optional<InterrogationSummary> findSummaryById(String interrogationId);

    List<InterrogationState> findWithStateByIds(List<String> interrogations);

    void delete(String interrogationId);

    InterrogationDepositProof getInterrogationDepositProof(String interrogationId);

    InterrogationMetadata getInterrogationMetadata(String interrogationId);

    InterrogationSummary getSummaryById(String interrogationId);

    List<Interrogation> findByIds(List<String> interrogationIds);

    List<Interrogation> findAllInterrogations();

    List<InterrogationState> getInterrogations(String campaignId, StateDataType stateDataType);

    List<Interrogation> getInterrogationsByState(StateDataType stateDataType);
}
