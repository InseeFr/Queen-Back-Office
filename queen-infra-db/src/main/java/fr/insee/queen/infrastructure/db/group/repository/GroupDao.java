package fr.insee.queen.infrastructure.db.group.repository;

import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupKind;
import fr.insee.queen.domain.group.model.GroupSummary;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.entity.GroupSummaryRow;
import fr.insee.queen.infrastructure.db.group.entity.MetadataDB;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.group.repository.jpa.GroupJpaRepository;
import fr.insee.queen.infrastructure.db.group.repository.jpa.QuestionnaireModelJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class GroupDao implements GroupRepository {

    private final GroupJpaRepository jpaRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Override
    public Optional<Group> findGroup(String groupId) {
        Optional<GroupDB> groupOpt = jpaRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return Optional.empty();
        }
        ObjectNode metadata = null;
        GroupDB group = groupOpt.get();
        if(group.getMetadata() != null) {
            metadata = group.getMetadata().getValue();
        }

        return Optional.of(new Group(
                group.getId(),
                group.getLabel(),
                group.getShortLabel(),
                group.getQuestionnaireModels()
                        .stream()
                        .map(QuestionnaireModelDB::getId)
                        .collect(Collectors.toSet()),
                metadata)
        );
    }

    @Override
    @Transactional
    public void create(Group group, GroupKind kind) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(group.getQuestionnaireIds());
        GroupDB groupDB = new GroupDB(group.getId(), group.getLabel(), group.getShortLabel(), questionnaireModels, kind.name());

        ObjectNode metadataValue = group.getMetadata();
        if (metadataValue != null) {
            MetadataDB m = new MetadataDB(metadataValue, groupDB);
            groupDB.setMetadata(m);
        }
        jpaRepository.save(groupDB);
    }

    @Override
    public boolean exists(String groupId) {
        return jpaRepository.existsById(groupId);
    }

    @Override
    public List<GroupSummary> getAllWithQuestionnaireIds() {
        return jpaRepository.findAllGroupSummaryRows().stream()
                .collect(Collectors.groupingBy(
                        GroupSummaryRow::groupId,
                        Collectors.collectingAndThen(Collectors.toList(), rows -> {
                            GroupSummaryRow first = rows.getFirst();
                            Set<String> questionnaireIds = rows.stream()
                                    .map(GroupSummaryRow::questionnaireId)
                                    .filter(Objects::nonNull) // éviter les null si pas de questionnaire
                                    .collect(Collectors.toSet());
                            return new GroupSummary(
                                    first.groupId(),
                                    first.label(),
                                    questionnaireIds
                            );
                        })
                ))
                .values()
                .stream()
                .toList();
    }



    @Override
    public void delete(String groupId) {
        jpaRepository.deleteById(groupId);
    }

    @Override
    public Optional<GroupSummary> findWithQuestionnaireIds(String groupId) {
        Optional<GroupDB> groupOpt = jpaRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return Optional.empty();
        }
        GroupDB group = groupOpt.get();
        return Optional.of(new GroupSummary(
                group.getId(),
                group.getLabel(),
                group.getQuestionnaireModels()
                        .stream()
                        .map(QuestionnaireModelDB::getId)
                        .collect(Collectors.toSet()))
        );
    }

    @Override
    @Transactional
    public void update(Group group) {
        GroupDB groupDB = jpaRepository.findById(group.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Group %s not found", group.getId())));
        groupDB.setLabel(group.getLabel());
        groupDB.setShortLabel(group.getShortLabel());

        ObjectNode metadataValue = group.getMetadata();
        MetadataDB metadata = groupDB.getMetadata();
        if (metadata == null) {
            metadata = new MetadataDB(metadataValue, groupDB);
            groupDB.setMetadata(metadata);
        } else {
            metadata.setValue(metadataValue);
        }
        groupDB.getQuestionnaireModels().clear();
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(group.getQuestionnaireIds());
        groupDB.setQuestionnaireModels(questionnaireModels);
        jpaRepository.save(groupDB);
    }

    @Override
    public Optional<ObjectNode> findMetadataByGroupId(String groupId) {
        return jpaRepository.findMetadataByGroupId(groupId);
    }

    @Override
    public List<String> getAllGroupIds() {
        return jpaRepository.findAllGroupIds();
    }
}
