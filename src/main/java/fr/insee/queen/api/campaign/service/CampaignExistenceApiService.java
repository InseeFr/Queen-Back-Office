package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.gateway.CampaignRepository;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.web.exception.EntityAlreadyExistException;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CampaignExistenceApiService implements CampaignExistenceService {
    private final CampaignRepository campaignRepository;
    private final CacheManager cacheManager;

    @Override
    public void throwExceptionIfCampaignNotExist(String campaignId) {
        if (!existsById(campaignId)) {
            throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
        }
    }

    @Override
    public void throwExceptionIfCampaignAlreadyExist(String campaignId) {
        if (existsById(campaignId)) {
            throw new EntityAlreadyExistException(String.format("Campaign %s already exist", campaignId));
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
