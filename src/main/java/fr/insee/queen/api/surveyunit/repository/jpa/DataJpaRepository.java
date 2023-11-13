package fr.insee.queen.api.surveyunit.repository.jpa;

import fr.insee.queen.api.surveyunit.repository.entity.DataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 */
@Repository
public interface DataJpaRepository extends JpaRepository<DataDB, UUID> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from data d where id in (
                select d.id from survey_unit s
                    where s.id = d.survey_unit_id
                    and s.campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteDatas(String campaignId);

    @Transactional
    @Modifying
    @Query("update DataDB d set d.value = :data where d.surveyUnit.id = :surveyUnitId")
    int updateData(String surveyUnitId, String data);

    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    Optional<String> findData(String surveyUnitId);

    void deleteBySurveyUnitId(String id);
}
