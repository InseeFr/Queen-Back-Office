package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.service.exception.CampaignServiceException;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
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
        if(!existsById(campaignId)) {
            throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
        }
    }

    @Override
    public void throwExceptionIfCampaignAlreadyExist(String campaignId) {
        if(existsById(campaignId)) {
            throw new CampaignServiceException(String.format("Campaign %s already exist", campaignId));
        }
    }

    @Override
    public boolean existsById(String campaignId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation
        Boolean isCampaignPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId, Boolean.class);
        if(isCampaignPresent != null) {
            return isCampaignPresent;
        }
        isCampaignPresent = campaignRepository.existsById(campaignId);
        Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).putIfAbsent(campaignId, isCampaignPresent);

        return isCampaignPresent;
    }
}
