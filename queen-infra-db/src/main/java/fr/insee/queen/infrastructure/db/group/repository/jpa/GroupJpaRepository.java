package fr.insee.queen.infrastructure.db.group.repository.jpa;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.entity.GroupSummaryRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository to handle groups
 */
@Repository
public interface GroupJpaRepository extends JpaRepository<GroupDB, String> {


    @Query("""
    select new fr.insee.queen.infrastructure.db.group.entity.GroupSummaryRow(
        c.id, c.label, q.id
    )
    from GroupDB c
    left join c.questionnaireModels q
    """)
    List<GroupSummaryRow> findAllGroupSummaryRows();

    /**
     * Retrieve group by id
     * @return {@link GroupDB} a group
     */
    @Query("select c from GroupDB c left join fetch c.metadata left join fetch c.questionnaireModels where c.id=:groupId")
    Optional<GroupDB> findById(String groupId);

    /**
     * Retrieve the metadata json value of a group
     * @param groupId group id
     * @return {@link ObjectNode} json metadata value
     */
    @Query("""
            select c.metadata.value
            from GroupDB c where c.id=:groupId""")
    Optional<ObjectNode> findMetadataByGroupId(String groupId);

    /**
     * Retrieve all groups ids
     * @return {@link String} all groups ids
     */
    @Query("SELECT c.id FROM GroupDB c")
    List<String> findAllGroupIds();
}
