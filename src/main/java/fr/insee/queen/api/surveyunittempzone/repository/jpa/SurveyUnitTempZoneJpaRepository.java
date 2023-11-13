package fr.insee.queen.api.surveyunittempzone.repository.jpa;

import fr.insee.queen.api.surveyunittempzone.repository.entity.SurveyUnitTempZoneDB;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
 *
 * @author Laurent Caouissin
 */
@Repository
public interface SurveyUnitTempZoneJpaRepository extends JpaRepository<SurveyUnitTempZoneDB, String> {

    void deleteBySurveyUnitId(String id);

    List<SurveyUnitTempZone> findAllProjectedBy();

    @Transactional
    @Modifying
    @Query(value = """
            delete from survey_unit_temp_zone st where id in (
                select st.id from survey_unit s
                    where s.id = st.survey_unit_id
                    and s.campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteSurveyUnits(String campaignId);

    @Transactional
    @Modifying
    @Query(value = """
            insert into survey_unit_temp_zone (id, survey_unit_id, user_id, date, survey_unit)
                values(:id, :surveyUnitId, :userId, :date, :surveyUnit\\:\\:jsonb)""", nativeQuery = true)
    void saveSurveyUnit(UUID id, String surveyUnitId, String userId, Long date, String surveyUnit);
}
