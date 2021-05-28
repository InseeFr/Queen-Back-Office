package fr.insee.queen.api.repository;

import java.util.Optional;
import java.util.UUID;

import fr.insee.queen.api.domain.Personalization;

/**
* ParadataEventRepository is the repository using to access to ParadataEvent table in DB
* 
* @author Corcaud Samuel
* 
*/
public interface PersonalizationRepository extends ApiRepository<Personalization, UUID> {

	Optional<Personalization> findBySurveyUnitId(String id);


}
