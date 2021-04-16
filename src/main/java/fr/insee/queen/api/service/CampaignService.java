package fr.insee.queen.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.campaign.CampaignResponseDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;

public interface CampaignService extends BaseService<Campaign, String> {

	List<CampaignDto> findDtoBy();

	Optional<Campaign> findById(String id);

	List<Campaign> findAll();

	void save(Campaign c);
	
	void saveDto(CampaignDto c);
	
	Boolean checkIfQuestionnaireOfCampaignExists(CampaignDto campaign);
		
	List<CampaignResponseDto> getAllCampaigns();
	
	List<QuestionnaireIdDto> getQuestionnaireIds(String id);
	
	List<QuestionnaireModelDto> getQuestionnaireModels(String id);
}
