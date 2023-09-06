package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnitTempZone;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
* 
* @author Laurent Caouissin
* 
*/
@Transactional
@Repository
public interface SurveyUnitTempZoneRepository extends JpaRepository<SurveyUnitTempZone, String> {

    void deleteBySurveyUnitId(String id);

    List<SurveyUnitTempZoneDto> findAllProjectedBy();
}
