package fr.insee.queen.domain.group.service;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.service.exception.QuestionnaireInvalidException;
import fr.insee.queen.domain.group.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireModelApiService implements QuestionnaireModelService {

    private final GroupExistenceService groupExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final NomenclatureService nomenclatureService;

    @Override
    public List<String> getQuestionnaireIds(String groupId) {
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        return questionnaireModelRepository.findAllIds(groupId);
    }

    @Override
    @Cacheable(CacheName.QUESTIONNAIRE)
    public ObjectNode getQuestionnaireData(String id) {
        return questionnaireModelRepository
                .findQuestionnaireData(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire data %s was not found", id)));
    }

    @Override
    @Transactional
    public void createQuestionnaire(QuestionnaireModel questionnaire) {
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireAlreadyExist(questionnaire.getId());

        if (!nomenclatureService.areNomenclaturesValid(questionnaire.getRequiredNomenclatureIds())) {
            throw new QuestionnaireInvalidException(String.format("Cannot create questionnaire model %s as some nomenclatures do not exist",
                    questionnaire.getId()));
        }

        questionnaireModelRepository.create(questionnaire);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.QUESTIONNAIRE_NOMENCLATURES, key = "#questionnaire.id"),
            @CacheEvict(value = CacheName.QUESTIONNAIRE, key = "#questionnaire.id"),
    })
    @Transactional
    public void updateQuestionnaire(QuestionnaireModel questionnaire) {
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaire.getId());

        if (!nomenclatureService.areNomenclaturesValid(questionnaire.getRequiredNomenclatureIds())) {
            throw new QuestionnaireInvalidException(String.format("Cannot update questionnaire model %s as some nomenclatures do not exist",
                    questionnaire.getId()));
        }
        questionnaireModelRepository.update(questionnaire);
    }

    @Override
    public List<ObjectNode> getQuestionnaireDatas(String groupId) {
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        return questionnaireModelRepository.findAllQuestionnaireDatas(groupId).stream()
                .toList();
    }
}
