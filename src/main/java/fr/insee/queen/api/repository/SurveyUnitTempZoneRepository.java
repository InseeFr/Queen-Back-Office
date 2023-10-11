package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnitTempZone;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
*
* @author Laurent Caouissin
*
*/
@Repository
public interface SurveyUnitTempZoneRepository extends JpaRepository<SurveyUnitTempZone, String> {

    void deleteBySurveyUnitId(String id);

    List<SurveyUnitTempZoneDto> findAllProjectedBy();

	@Modifying
    @Query(value = """
	delete from survey_unit_temp_zone st where id in (
	    select st.id from survey_unit s
	        where s.id = st.survey_unit_id
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
    void deleteSurveyUnits(String campaignId);
}
