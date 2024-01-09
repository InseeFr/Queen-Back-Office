package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CampaignExistenceApiService implements CampaignExistenceService {
    private final CampaignRepository campaignRepository;
    private final CacheManager cacheManager;
    public static final String NOT_FOUND_MESSAGE = "Campaign %s was not found";
    public static final String ALREADY_EXIST_MESSAGE = "Campaign %s already exist";

    @Override
    public void throwExceptionIfCampaignNotExist(String campaignId) {
        if (!existsById(campaignId)) {
            throw new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, campaignId));
        }
    }

    @Override
    public void throwExceptionIfCampaignAlreadyExist(String campaignId) {
        if (existsById(campaignId)) {
            throw new EntityAlreadyExistException(String.format(ALREADY_EXIST_MESSAGE, campaignId));
        }
    }

    @Override
    public boolean existsById(String campaignId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation
        Boolean isCampaignPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId, Boolean.class);
        if (isCampaignPresent != null) {
            return isCampaignPresent;
        }
        isCampaignPresent = campaignRepository.exists(campaignId);
        Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).putIfAbsent(campaignId, isCampaignPresent);

        return isCampaignPresent;
    }
}
