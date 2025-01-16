package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.MetadataService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.model.*;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
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
public class SurveyUnitApiService implements SurveyUnitService {
    public static final String NOT_FOUND_MESSAGE = "Survey unit %s was not found";
    public static final String ALREADY_EXIST_MESSAGE = "Survey unit %s already exists";
    private final SurveyUnitRepository surveyUnitRepository;
    private final StateDataService stateDataService;
    private final DataService dataService;
    private final CampaignExistenceService campaignExistenceService;
    private final MetadataService metadataService;
    private final CacheManager cacheManager;

    @Override
    public boolean existsById(String surveyUnitId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation
        Boolean isSurveyUnitPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId, Boolean.class);
        if (isSurveyUnitPresent != null) {
            return isSurveyUnitPresent;
        }
        isSurveyUnitPresent = surveyUnitRepository.exists(surveyUnitId);
        Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).putIfAbsent(surveyUnitId, isSurveyUnitPresent);

        return isSurveyUnitPresent;
    }

    @Override
    public void throwExceptionIfSurveyUnitNotExist(String surveyUnitId) {
        if (!existsById(surveyUnitId)) {
            throw new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, surveyUnitId));
        }
    }

    @Override
    public void throwExceptionIfSurveyUnitExist(String surveyUnitId) {
        if (existsById(surveyUnitId)) {
            throw new EntityAlreadyExistException(String.format(ALREADY_EXIST_MESSAGE, surveyUnitId));
        }
    }

    @Override
    public SurveyUnit getSurveyUnit(String id) {
        return surveyUnitRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByCampaignId(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return surveyUnitRepository.findAllSummaryByCampaignId(campaignId);
    }

    @Override
    public List<SurveyUnit> findByIds(List<String> surveyUnitIds) {
        return surveyUnitRepository.find(surveyUnitIds);
    }

    @Override
    public List<SurveyUnit> findAllSurveyUnits() {
        return surveyUnitRepository.findAll();
    }

    @Override
    public List<String> findAllSurveyUnitIds() {
        return surveyUnitRepository.findAllIds()
                .orElseThrow(() -> new EntityNotFoundException("List of survey unit ids not found"));
    }

    @Transactional
    @Override
    public void updateSurveyUnit(SurveyUnit surveyUnit) {
        throwExceptionIfSurveyUnitNotExist(surveyUnit.id());
        StateData newStateData = surveyUnit.stateData();

        surveyUnitRepository.updateInfos(surveyUnit);
        if (newStateData == null) {
            return;
        }
        try {
            stateDataService.saveStateData(surveyUnit.id(), newStateData);
        } catch (StateDataInvalidDateException ex) {
            // in the case of survey unit update, a problem with state data does not require to
            // rollback the other updates on survey unit
            log.warn(String.format("%s - %s", surveyUnit.id(), ex.getMessage()));
        }
    }

    @Transactional
    @Override
    public void updateSurveyUnit(String surveyUnitId, ObjectNode collectedDataToUpdate, StateData stateData) {
        if(collectedDataToUpdate != null && ! collectedDataToUpdate.isEmpty()) {
            dataService.updateCollectedData(surveyUnitId, collectedDataToUpdate);
        }

        try {
            stateDataService.saveStateData(surveyUnitId, stateData);
        } catch (StateDataInvalidDateException ex) {
            // in the case of survey unit update, a problem with state collectedDataToUpdate does not require to
            // rollback the other updates on survey unit
            log.warn(String.format("%s - %s", surveyUnitId, ex.getMessage()));
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheName.SURVEY_UNIT_EXIST, key = "#surveyUnit.id")
    public void createSurveyUnit(SurveyUnit surveyUnit) throws StateDataInvalidDateException {
        throwExceptionIfSurveyUnitExist(surveyUnit.id());
        campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(surveyUnit.campaignId(), surveyUnit.questionnaireId());
        surveyUnitRepository.create(surveyUnit);
        StateData stateData = surveyUnit.stateData();
        if(stateData != null) {
            stateDataService.saveStateData(surveyUnit.id(), surveyUnit.stateData());
        }
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByIds(List<String> surveyUnits) {
        return surveyUnitRepository.findAllSummaryByIdIn(surveyUnits);
    }

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation (some internal methods call this one)
        SurveyUnitSummary surveyUnitSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId, SurveyUnitSummary.class);

        if (surveyUnitSummary != null) {
            return Optional.of(surveyUnitSummary);
        }

        Optional<SurveyUnitSummary> summaryOptional = surveyUnitRepository.findSummaryById(surveyUnitId);
        summaryOptional.ifPresent(summary ->
                Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).putIfAbsent(surveyUnitId, summary));
        return summaryOptional;
    }

    @Override
    public List<SurveyUnitState> findWithStateByIds(List<String> surveyUnits) {
        return surveyUnitRepository.findAllWithStateByIdIn(surveyUnits);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(CacheName.SURVEY_UNIT_EXIST),
            @CacheEvict(CacheName.SURVEY_UNIT_SUMMARY)
    })
    public void delete(String surveyUnitId) {
        throwExceptionIfSurveyUnitNotExist(surveyUnitId);
        surveyUnitRepository.delete(surveyUnitId);
    }

    @Override
    public SurveyUnitDepositProof getSurveyUnitDepositProof(String surveyUnitId) {
        return surveyUnitRepository
                .findWithCampaignAndStateById(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, surveyUnitId)));
    }

    @Override
    public SurveyUnitMetadata getSurveyUnitMetadata(String surveyUnitId) {
        SurveyUnitPersonalization surveyUnitPersonalization =
                surveyUnitRepository.getSurveyUnitPersonalization(surveyUnitId);
        ObjectNode metadata = metadataService.getMetadataByQuestionnaireId(surveyUnitPersonalization.questionnaireId());

        return SurveyUnitMetadata.create(surveyUnitPersonalization, metadata);
    }

    @Override
    public SurveyUnitSummary getSummaryById(String surveyUnitId) {
        return findSummaryById(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, surveyUnitId)));
    }
}
