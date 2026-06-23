package fr.insee.queen.infrastructure.db.group.repository.jpa;

import tools.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.group.model.Nomenclature;
import fr.insee.queen.infrastructure.db.group.entity.NomenclatureDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Jpa repository to handle nomenclatures
 */
@Repository
public interface NomenclatureJpaRepository extends JpaRepository<NomenclatureDB, String> {
    Optional<Nomenclature> findNomenclatureById(String id);

    @Query("select n.id from NomenclatureDB n order by n.id asc")
    Optional<List<String>> findAllNomenclatureIds();

    Set<NomenclatureDB> findAllByIdIn(Set<String> ids);

    @Transactional
    @Modifying
    @Query("update NomenclatureDB n set n.label=:label, n.value=:value where n.id = :id")
    void updateNomenclature(String id, String label, ArrayNode value);

    @Transactional
    @Modifying
    @NativeQuery("""
            INSERT INTO nomenclature (id, label, value)
            VALUES (:id, :label, :value\\:\\:jsonb)""")
    void createNomenclature(String id, String label, ArrayNode value);

    @Query("""
                select distinct n.id from GroupDB g join g.questionnaireModels qm inner join qm.nomenclatures n where g.id = :groupId
            """)
    List<String> findRequiredNomenclatureByGroupId(String groupId);

    @Query("""
                select n.id from QuestionnaireModelDB qm inner join qm.nomenclatures n where qm.id=:questionnaireId
            """)
    List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);
}
