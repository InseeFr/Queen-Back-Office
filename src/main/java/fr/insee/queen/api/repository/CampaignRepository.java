package fr.insee.queen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.campaign.CampaignDto;

/**
* CampaignRepository is the repository using to access to Campaign table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface CampaignRepository extends JpaRepository<Campaign, String> {
	/**
	* This method retrieve all Campaign in DB
	* 
	* @return List of all {@link CampaignDto}
	*/
	List<CampaignDto> findDtoBy();
}
