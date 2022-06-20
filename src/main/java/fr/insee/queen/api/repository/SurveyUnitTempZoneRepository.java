package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnitTempZone;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;

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

}
