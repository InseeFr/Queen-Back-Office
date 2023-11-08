package fr.insee.queen.api.repository.jpa;

import fr.insee.queen.api.repository.entity.PersonalizationDB;
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
public interface PersonalizationJpaRepository extends JpaRepository<PersonalizationDB, UUID> {
    @Transactional
    @Modifying
    @Query(value = """
	delete from personalization p where id in (
	    select p.id from survey_unit s
	        where s.id = p.survey_unit_id
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
    void deletePersonalizations(String campaignId);

	@Transactional
	@Modifying
	@Query("update PersonalizationDB p set p.value = :personalization where p.surveyUnit.id = :surveyUnitId")
	void updatePersonalization(String surveyUnitId, String personalization);

	@Query("select s.personalization.value from SurveyUnitDB s where s.id=:surveyUnitId")
	String getPersonalization(String surveyUnitId);

	void deleteBySurveyUnitId(String id);
}
