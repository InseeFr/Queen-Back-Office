package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MetadataService {

	private final CampaignRepository campaignRepository;

	public MetadataDto getMetadata(String campaignId) throws EntityNotFoundException {
		return campaignRepository.findMetadataByCampaignId(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for campaign %s was not found", campaignId)));
	}

	@Cacheable(CacheName.METADATA_BY_QUESTIONNAIRE)
	public MetadataDto getMetadataByQuestionnaireId(String questionnaireId) {
		return campaignRepository.findMetadataByQuestionnaireId(questionnaireId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for questionnaire %s was not found", questionnaireId)));
	}
}
