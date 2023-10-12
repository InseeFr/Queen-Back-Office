package fr.insee.queen.api.service;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.NomenclatureRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class NomenclatureService {
	private final NomenclatureRepository nomenclatureRepository;

	@Cacheable(CacheName.NOMENCLATURE)
	public NomenclatureDto getNomenclature(String id) {
		return nomenclatureRepository.findNomenclatureById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Nomenclature %s was not found", id)));
	}

	public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
		if(nomenclatureIds.isEmpty()) {
			return true;
		}
		return nomenclatureIds.stream().anyMatch(nomenclatureId ->
			nomenclatureRepository
					.findById(nomenclatureId)
					.isPresent()
		);
	}
	
	public Set<Nomenclature> findAllByIds(Set<String> nomenclatureIds){
		return nomenclatureRepository.findAllByIdIn(nomenclatureIds);
	}

	@CacheEvict(value = CacheName.NOMENCLATURE, key = "#nomenclature.id")
	public void saveNomenclature(NomenclatureInputDto nomenclature) {
		Optional<Nomenclature> nomenclatureOptional = nomenclatureRepository.findById(nomenclature.id());

    	if(nomenclatureOptional.isPresent()){
			log.info("Update nomenclature" + nomenclature.id());
			Nomenclature nomenclatureDB = nomenclatureOptional.get();
			nomenclatureDB.value(nomenclature.value().toString());
			nomenclatureRepository.save(nomenclatureDB);
			return;
		}

		log.info("Create new nomenclature" + nomenclature.id());
		Nomenclature newNomenclature = new Nomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
		nomenclatureRepository.save(newNomenclature);
	}

	public List<String> getAllNomenclatureIds() {
		return nomenclatureRepository.findAllNomenclatureIds()
				.orElseThrow(() -> new EntityNotFoundException("No nomenclatures found"));
	}
}
