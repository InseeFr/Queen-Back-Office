package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnitTempZone;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
* SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
* 
* @author Laurent Caouissin
* 
*/
@Transactional
@Repository
public interface SurveyUnitTempZoneRepository extends ApiRepository<SurveyUnitTempZone, String> {

}
