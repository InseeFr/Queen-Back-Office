package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import fr.insee.queen.api.domain.ParadataEvent;

public interface ParadataEventService extends BaseService<ParadataEvent, UUID> {

	void save(ParadataEvent paradataEvent);

	Optional<ParadataEvent> findById(UUID uuid);

    
}
