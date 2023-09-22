package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.ParadataEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
* ParadataEventRepository is the repository using to access to ParadataEvent table in DB
* 
* @author Corcaud Samuel
* 
*/
@Repository
public interface ParadataEventRepository extends JpaRepository<ParadataEvent, UUID> {

    @Modifying
    @Query(value = """
		INSERT INTO paradata_event (id, value, survey_unit_id)
		VALUES (:id,:paradataValue,:surveyUnitId)""", nativeQuery = true)
    void createParadataEvent(UUID id, String paradataValue, String surveyUnitId);

    @Modifying
    @Query(value = """
	delete from paradata_event p where id in (
	    select p.id from survey_unit s
	        where text(s.id) = p.value->>'idSU'
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
    void deleteParadataEvents(String campaignId);
}
