package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.MetadataService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterrogationApiService implements InterrogationService {
    public static final String NOT_FOUND_MESSAGE = "Interrogation %s was not found";
    public static final String ALREADY_EXIST_MESSAGE = "Interrogation %s already exists";
    private final InterrogationRepository interrogationRepository;
    private final StateDataService stateDataService;
    private final DataService dataService;
    private final CampaignExistenceService campaignExistenceService;
    private final MetadataService metadataService;
    private final CacheManager cacheManager;

    @Override
    public boolean existsById(String interrogationId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation
        Boolean isInterrogationPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId, Boolean.class);
        if (isInterrogationPresent != null) {
            return isInterrogationPresent;
        }
        isInterrogationPresent = interrogationRepository.exists(interrogationId);
        Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).putIfAbsent(interrogationId, isInterrogationPresent);

        return isInterrogationPresent;
    }

    @Override
    public void throwExceptionIfInterrogationNotExist(String interrogationId) {
        if (!existsById(interrogationId)) {
            throw new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, interrogationId));
        }
    }

    @Override
    public void throwExceptionIfInterrogationExist(String interrogationId) {
        if (existsById(interrogationId)) {
            throw new EntityAlreadyExistException(String.format(ALREADY_EXIST_MESSAGE, interrogationId));
        }
    }

    @Override
    public Interrogation getInterrogation(String id) {
        return interrogationRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public List<InterrogationSummary> findSummariesByCampaignId(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return interrogationRepository.findAllSummaryByCampaignId(campaignId);
    }

    @Override
    public List<Interrogation> findByIds(List<String> interrogationIds) {
        return interrogationRepository.find(interrogationIds);
    }

    @Override
    public List<Interrogation> findAllInterrogations() {
        return interrogationRepository.findAll();
    }

    @Override
    public List<String> findAllInterrogationIds() {
        return interrogationRepository.findAllIds()
                .orElseThrow(() -> new EntityNotFoundException("List of interrogation ids not found"));
    }

    @Override
    public List<InterrogationState> getInterrogations(String campaignId, StateDataType stateDataType) {
        return interrogationRepository.findAllByState(campaignId, stateDataType);
    }

    @Transactional
    @Override
    public void updateInterrogation(Interrogation interrogation) {
        throwExceptionIfInterrogationNotExist(interrogation.id());
        StateData newStateData = interrogation.stateData();

        interrogationRepository.update(interrogation);
        if (newStateData == null) {
            return;
        }
        try {
            stateDataService.saveStateData(interrogation.id(), newStateData);
        } catch (StateDataInvalidDateException ex) {
            // in the case of interrogation update, a problem with state data does not require to
            // rollback the other updates on interrogation
            log.warn(String.format("%s - %s", interrogation.id(), ex.getMessage()));
        }
    }

    @Transactional
    @Override
    public void updateInterrogation(String interrogationId, ObjectNode collectedDataToUpdate, StateData stateData) {
        if(collectedDataToUpdate != null && ! collectedDataToUpdate.isEmpty()) {
            dataService.updateCollectedData(interrogationId, collectedDataToUpdate);
        }

        try {
            stateDataService.saveStateData(interrogationId, stateData);
        } catch (StateDataInvalidDateException ex) {
            // in the case of interrogation update, a problem with state collectedDataToUpdate does not require to
            // rollback the other updates on interrogation
            log.warn(String.format("%s - %s", interrogationId, ex.getMessage()));
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheName.INTERROGATION_EXIST, key = "#interrogation.id")
    public void createInterrogation(Interrogation interrogation) throws StateDataInvalidDateException {
        throwExceptionIfInterrogationExist(interrogation.id());
        campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(interrogation.campaignId(), interrogation.questionnaireId());
        interrogationRepository.create(interrogation);
        StateData stateData = interrogation.stateData();
        if(stateData != null) {
            stateDataService.saveStateData(interrogation.id(), interrogation.stateData());
        }
    }

    @Override
    public List<InterrogationSummary> findSummariesByIds(List<String> interrogations) {
        return interrogationRepository.findAllSummaryByIdIn(interrogations);
    }

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation (some internal methods call this one)
        InterrogationSummary interrogationSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId, InterrogationSummary.class);

        if (interrogationSummary != null) {
            return Optional.of(interrogationSummary);
        }

        Optional<InterrogationSummary> summaryOptional = interrogationRepository.findSummaryById(interrogationId);
        summaryOptional.ifPresent(summary ->
                Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).putIfAbsent(interrogationId, summary));
        return summaryOptional;
    }

    @Override
    public List<InterrogationState> findWithStateByIds(List<String> interrogations) {
        return interrogationRepository.findAllWithStateByIdIn(interrogations);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(CacheName.INTERROGATION_EXIST),
            @CacheEvict(CacheName.INTERROGATION_SUMMARY)
    })
    public void delete(String interrogationId) {
        throwExceptionIfInterrogationNotExist(interrogationId);
        interrogationRepository.delete(interrogationId);
    }

    @Override
    public InterrogationDepositProof getInterrogationDepositProof(String interrogationId) {
        return interrogationRepository
                .findWithCampaignAndStateById(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, interrogationId)));
    }

    @Override
    public InterrogationMetadata getInterrogationMetadata(String interrogationId) {
        InterrogationPersonalization interrogationPersonalization =
                interrogationRepository.getInterrogationPersonalization(interrogationId);
        ObjectNode metadata = metadataService.getMetadataByQuestionnaireId(interrogationPersonalization.questionnaireId());

        return InterrogationMetadata.create(interrogationPersonalization, metadata);
    }

    @Override
    public InterrogationSummary getSummaryById(String interrogationId) {
        return findSummaryById(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, interrogationId)));
    }
}
