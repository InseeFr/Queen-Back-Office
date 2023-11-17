package fr.insee.queen.api.surveyunittempzone.repository.jpa;

import fr.insee.queen.api.surveyunittempzone.repository.entity.SurveyUnitTempZoneDB;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA repository to handle survey units in temp zone
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
            delete from survey_unit_temp_zone where survey_unit_id in (
                select id from survey_unit
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteSurveyUnits(String campaignId);
}
