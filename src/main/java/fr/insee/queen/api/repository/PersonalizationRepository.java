package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.dto.personalization.PersonalizationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
* ParadataEventRepository is the repository using to access to ParadataEvent table in DB
* 
* @author Corcaud Samuel
* 
*/
@Repository
public interface PersonalizationRepository extends JpaRepository<Personalization, UUID> {

	Optional<PersonalizationDto> findBySurveyUnitId(String id);

	@Modifying
	@Query("update Personalization p set p.value = :personalizationValue where p.id = :surveyUnitId")
	void updatePersonalization(String surveyUnit, String personalizationValue);
}
