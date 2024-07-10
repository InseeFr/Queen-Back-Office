package fr.insee.queen.infrastructure.db.surveyunit.repository.jpa;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.PersonalizationDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle survey units personalization data
 */
@Repository
public interface PersonalizationJpaRepository extends JpaRepository<PersonalizationDB, UUID> {
    /**
     * Delete all survey units personalization for a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from personalization where survey_unit_id in (
                select id from survey_unit
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deletePersonalizations(String campaignId);

    /**
     * Update personalization for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param personalization json personalization to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update PersonalizationDB p set p.value = :personalization where p.surveyUnit.id = :surveyUnitId")
    int updatePersonalization(String surveyUnitId, ArrayNode personalization);

    /**
     * Find the personalization of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the personalization (json format)
     */
    @Query("select s.personalization.value from SurveyUnitDB s where s.id=:surveyUnitId")
    Optional<ArrayNode> findPersonalization(String surveyUnitId);

    /**
     * Delete personalization of a survey unit
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    void deleteBySurveyUnitId(String surveyUnitId);
}
