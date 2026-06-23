package fr.insee.queen.infrastructure.db.group.repository;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.entity.NomenclatureDB;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.group.repository.jpa.GroupJpaRepository;
import fr.insee.queen.infrastructure.db.group.repository.jpa.NomenclatureJpaRepository;
import fr.insee.queen.infrastructure.db.group.repository.jpa.QuestionnaireModelJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class QuestionnaireModelDao implements QuestionnaireModelRepository {
    private final QuestionnaireModelJpaRepository jpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final NomenclatureJpaRepository nomenclatureRepository;

    @Override
    public List<String> findAllIds(String groupId) {
        return jpaRepository.findAllIdByGroupId(groupId);
    }

    @Override
    public Optional<ObjectNode> findQuestionnaireData(String questionnaireId) {
        return jpaRepository.findQuestionnaireData(questionnaireId);
    }

    @Override
    public boolean exists(String questionnaireId) {
        return jpaRepository.existsById(questionnaireId);
    }

    @Override
    @Transactional
    public void create(QuestionnaireModel questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.getRequiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.getId(), questionnaireData.getLabel(), questionnaireData.getValue(), requiredNomenclatures);
        if (questionnaireData.getGroupId() != null) {
            GroupDB group = groupJpaRepository.getReferenceById(questionnaireData.getGroupId());
            questionnaire.setGroup(group);
        }
        jpaRepository.save(questionnaire);
    }

    @Override
    public void update(QuestionnaireModel questionnaireData) {
        QuestionnaireModelDB questionnaire = jpaRepository.getReferenceById(questionnaireData.getId());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.getRequiredNomenclatureIds());
        questionnaire.setLabel(questionnaireData.getLabel());
        questionnaire.setValue(questionnaireData.getValue());
        questionnaire.setNomenclatures(requiredNomenclatures);
        GroupDB group = groupJpaRepository.getReferenceById(questionnaireData.getGroupId());
        questionnaire.setGroup(group);

        jpaRepository.save(questionnaire);
    }

    @Override
    public Long countValidQuestionnaires(String groupId, Set<String> questionnaireIds) {
        return jpaRepository.countValidQuestionnairesByIds(groupId, questionnaireIds);
    }

    @Override
    public void deleteAllFromGroup(String groupId) {
        jpaRepository.deleteAllByGroupId(groupId);
    }

    @Override
    public List<ObjectNode> findAllQuestionnaireDatas(String groupId) {
        return jpaRepository.findAllValueByGroupId(groupId);
    }
}
