package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.exception.NotFoundException;

public interface MetadataService extends BaseService<Metadata, UUID> {

	void save(Metadata paradataEvent);

	MetadataDto findDtoByCampaignId(String id) throws NotFoundException;

	Optional<Metadata> findById(UUID uuid);

    
}
