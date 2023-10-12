package fr.insee.queen.api.service;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.MetadataRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MetadataService {

	private final MetadataRepository metadataRepository;

	public MetadataDto getMetadata(String campaignId) throws EntityNotFoundException {
		return metadataRepository.findByCampaignId(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for campaign %s was not found", campaignId)));
	}

	@Cacheable(CacheName.METADATA)
	public MetadataDto getMetadataByQuestionnaireId(String questionnaireId) {
		return metadataRepository.findByQuestionnaireId(questionnaireId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for questionnaire %s was not found", questionnaireId)));
	}
}
