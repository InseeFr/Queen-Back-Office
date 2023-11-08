package fr.insee.queen.api.repository.jpa;

import fr.insee.queen.api.repository.entity.CommentDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 *
 */
@Repository
public interface CommentJpaRepository extends JpaRepository<CommentDB, UUID> {
    @Transactional
    @Modifying
    @Query(value = """
	delete from comment c where id in (
	    select c.id from survey_unit s
	        where s.id = c.survey_unit_id
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
    void deleteComments(String campaignId);

	@Transactional
	@Modifying
	@Query("update CommentDB c set c.value = :comment where c.surveyUnit.id = :surveyUnitId")
	void updateComment(String surveyUnitId, String comment);

	@Query("select s.comment.value from SurveyUnitDB s where s.id=:surveyUnitId")
	String getComment(String surveyUnitId);

	void deleteBySurveyUnitId(String id);
}
