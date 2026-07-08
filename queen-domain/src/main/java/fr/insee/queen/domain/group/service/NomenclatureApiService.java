package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.gateway.NomenclatureRepository;
import fr.insee.queen.domain.group.model.Nomenclature;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class NomenclatureApiService implements NomenclatureService {
    private final NomenclatureRepository nomenclatureRepository;
    private final GroupExistenceService groupExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;

    @Override
    @Cacheable(CacheName.NOMENCLATURE)
    public Nomenclature getNomenclature(String id) {
        return nomenclatureRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nomenclature %s was not found", id)));
    }

    @Override
    public boolean existsById(String id) {
        return nomenclatureRepository.exists(id);
    }

    @Override
    public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
        if (nomenclatureIds.isEmpty()) {
            return true;
        }
        return nomenclatureIds.stream().allMatch(nomenclatureRepository::exists);
    }

    @Override
    @CacheEvict(value = CacheName.NOMENCLATURE, key = "#nomenclature.id")
    public void saveNomenclature(Nomenclature nomenclature) {
        if (nomenclatureRepository.exists(nomenclature.id())) {
            log.info("Update nomenclature: {}", nomenclature.id());
            nomenclatureRepository.update(nomenclature);
            return;
        }

        log.info("Create new nomenclature: {}", nomenclature.id());
        nomenclatureRepository.create(nomenclature);
    }

    @Override
    public List<String> getAllNomenclatureIds() {
        return nomenclatureRepository.findAllIds()
                .orElseThrow(() -> new EntityNotFoundException("No nomenclatures found"));
    }

    @Override
    public List<String> findRequiredNomenclatureByGroup(String groupId) {
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        return nomenclatureRepository.findRequiredNomenclatureByGroupId(groupId);
    }

    @Override
    @Cacheable(CacheName.QUESTIONNAIRE_NOMENCLATURES)
    public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId) {
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaireId);
        return nomenclatureRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireId);
    }
}
