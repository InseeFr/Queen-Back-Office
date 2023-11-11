package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.gateway.CampaignRepository;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MetadataApiService implements MetadataService {

    private final CampaignRepository campaignRepository;

    public String getMetadata(String campaignId) {
        return campaignRepository.findMetadataByCampaignId(campaignId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for campaign %s was not found", campaignId)));
    }

    @Cacheable(CacheName.QUESTIONNAIRE_METADATA)
    public String getMetadataByQuestionnaireId(String questionnaireId) {
        return campaignRepository.findMetadataByQuestionnaireId(questionnaireId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for questionnaire %s was not found", questionnaireId)));
    }
}
