package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.interrogation.entity.CommentDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle an interrogation's comment
 */
@Repository
public interface CommentJpaRepository extends JpaRepository<CommentDB, UUID> {
    /**
     * Delete all interrogations comment for a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from comment where interrogation_id in (
                select id from interrogation
                where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteComments(String campaignId);

    /**
     * Update comment for an interrogation
     *
     * @param interrogationId interrogation id
     * @param comment json comment to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update CommentDB c set c.value = :comment where c.interrogation.id = :interrogationId")
    int updateComment(String interrogationId, ObjectNode comment);

    /**
     * Find the comment of an interrogation
     *
     * @param interrogationId interrogation id
     * @return an optional of the comment (json format)
     */
    @Query("select s.comment.value from InterrogationDB s where s.id=:interrogationId")
    Optional<ObjectNode> findComment(String interrogationId);

    /**
     * Delete comment of an interrogation
     * @param interrogationId interrogation id
     */
    @Transactional
    @Modifying
    void deleteByInterrogationId(String interrogationId);
}
