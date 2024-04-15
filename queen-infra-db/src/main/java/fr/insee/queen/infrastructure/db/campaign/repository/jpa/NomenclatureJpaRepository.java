package fr.insee.queen.infrastructure.db.campaign.repository.jpa;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.infrastructure.db.campaign.entity.NomenclatureDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    @Query(value = """
            INSERT INTO nomenclature (id, label, value)
            VALUES (:id, :label, :value\\:\\:jsonb)""", nativeQuery = true)
    void createNomenclature(String id, String label, ArrayNode value);

    @Query("""
                select distinct n.id from QuestionnaireModelDB qm inner join qm.nomenclatures n where qm.campaign.id=:campaignId
            """)
    List<String> findRequiredNomenclatureByCampaignId(String campaignId);

    @Query("""
                select n.id from QuestionnaireModelDB qm inner join qm.nomenclatures n where qm.id=:questionnaireId
            """)
    List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);
}
