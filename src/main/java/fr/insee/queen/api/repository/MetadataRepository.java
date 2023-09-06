package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
* MetadataRepository is the repository using to access to Metadata table in DB
* 
* @author Corcaud Samuel
* 
*/
@Repository
public interface MetadataRepository extends JpaRepository<Metadata, UUID> {
	
	/**
	* This method retrieve the Metadata for a specific campaign
	* 
	* @param id the id of campaign
	* @return {@link MetadataDto}
	*/
	Optional<MetadataDto> findByCampaignId(String id);

	@Query("""
		select new fr.insee.queen.api.dto.metadata.MetadataDto(m.value)
		from Metadata m INNER JOIN m.campaign.questionnaireModels qm
		where qm.id=:questionnaireId""")
	Optional<MetadataDto> findByQuestionnaireId(String questionnaireId);
}
