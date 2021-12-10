package fr.insee.queen.api.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.queen.api.domain.ParadataEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.repository.ParadataEventRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.ParadataEventService;

@Service
public class ParadataEventServiceImpl extends AbstractService<ParadataEvent, UUID> implements ParadataEventService {

    protected final ParadataEventRepository paradataEventRepository;

    @Autowired
    public ParadataEventServiceImpl(ParadataEventRepository repository) {
        this.paradataEventRepository = repository;
    }

    @Override
    protected JpaRepository<ParadataEvent, UUID> getRepository() {
        return paradataEventRepository;
    }

	@Override
	public void save(ParadataEvent paradataEvent) {
		paradataEventRepository.save(paradataEvent);
	}

	@Override
	public Optional<ParadataEvent> findById(UUID id) {
		return paradataEventRepository.findById(id);
	}
}
