package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;
import fr.insee.queen.api.repository.jpa.SurveyUnitTempZoneJpaRepository;
import fr.insee.queen.api.service.gateway.SurveyUnitTempZoneRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
* SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
*
* @author Laurent Caouissin
*
*/
@Repository
@AllArgsConstructor
public class SurveyUnitTempZoneDao implements SurveyUnitTempZoneRepository {
	private final SurveyUnitTempZoneJpaRepository jpaRepository;

	@Override
	public void delete(String surveyUnitId) {
		jpaRepository.deleteBySurveyUnitId(surveyUnitId);
	}

	@Override
	public List<SurveyUnitTempZoneDto> getAllSurveyUnits() {
		return jpaRepository.findAllProjectedBy();
	}

	@Override
	public void save(UUID id, String surveyUnitId, String userId, Long date, String surveyUnit) {
		jpaRepository.saveSurveyUnit(id, surveyUnitId, userId, date, surveyUnit);
	}
}
