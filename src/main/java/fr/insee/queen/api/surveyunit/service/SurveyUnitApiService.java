package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.campaign.service.CampaignExistenceService;
import fr.insee.queen.api.campaign.service.QuestionnaireModelApiExistenceService;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.service.gateway.SurveyUnitRepository;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
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
    private final SurveyUnitRepository surveyUnitRepository;
    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelApiExistenceService questionnaireModelExistenceService;
    private final CacheManager cacheManager;

    public static final String SURVEY_UNIT_NOT_FOUND_LABEL = "Survey unit %s was not found";

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

    public void throwExceptionIfSurveyUnitNotExist(String surveyUnitId) {
        if (!existsById(surveyUnitId)) {
            throw new EntityNotFoundException(String.format(SURVEY_UNIT_NOT_FOUND_LABEL, surveyUnitId));
        }
    }

    @Override
    public SurveyUnit getSurveyUnit(String id) {
        return surveyUnitRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(SURVEY_UNIT_NOT_FOUND_LABEL, id)));
    }

    @Override
    public List<SurveyUnitSummary> findByCampaignId(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return surveyUnitRepository.findAllSummaryByCampaignId(campaignId);
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

        surveyUnitRepository.updateInfos(surveyUnit);
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheName.SURVEY_UNIT_EXIST, key = "#surveyUnit.id")
    public void createSurveyUnit(SurveyUnit surveyUnit) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(surveyUnit.campaignId());
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(surveyUnit.questionnaireId());
        surveyUnitRepository.create(surveyUnit);
    }

    @Override
    public List<SurveyUnitSummary> findSummaryByIds(List<String> surveyUnits) {
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
                .orElseThrow(() -> new EntityNotFoundException(String.format(SURVEY_UNIT_NOT_FOUND_LABEL, surveyUnitId)));
    }

    @Override
    public SurveyUnitSummary getSurveyUnitWithCampaignById(String surveyUnitId) {
        return findSummaryById(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(SURVEY_UNIT_NOT_FOUND_LABEL, surveyUnitId)));
    }
}
