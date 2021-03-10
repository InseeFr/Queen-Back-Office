package fr.insee.queen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.dto.metadata.MetadataDto;

/**
* MetadataRepository is the repository using to access to Metadata table in DB
* 
* @author Corcaud Samuel
* 
*/
public interface MetadataRepository extends JpaRepository<Metadata, Integer> {
	
	/**
	* This method retrieve the Metadata for a specific campaign
	* 
	* @param id the id of campaign
	* @return {@link MetadataDto}
	*/
	MetadataDto findDtoByCampaign_id(String id);

}
