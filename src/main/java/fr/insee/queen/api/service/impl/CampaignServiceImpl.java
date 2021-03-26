package fr.insee.queen.api.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.repository.ApiRepository;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.CampaignService;

@Service
public class CampaignServiceImpl extends AbstractService<Campaign, String> implements CampaignService {

    protected final CampaignRepository campaignRepository;
    
    protected final QuestionnaireModelRepository questionnaireModelRepository;

    @Autowired
    public CampaignServiceImpl(CampaignRepository repository, QuestionnaireModelRepository questionnaireModelRepository) {
        this.campaignRepository = repository;
        this.questionnaireModelRepository = questionnaireModelRepository;
    }

    @Override
    protected ApiRepository<Campaign, String> getRepository() {
        return campaignRepository;
    }

	@Override
	public List<CampaignDto> findDtoBy() {
		return campaignRepository.findDtoBy();
	}

	@Override
	public Optional<Campaign> findById(String id) {
		return campaignRepository.findById(id);
	}

	@Override
	public List<Campaign> findAll() {
		return campaignRepository.findAll();
	}

	@Override
	public void save(Campaign c) {
		campaignRepository.save(c);
	}
	
	@Override
	public void saveDto(CampaignDto c) {
		Set<QuestionnaireModel> qm = new HashSet<>();
		c.getQuestionnaireModelsIds().stream().forEach(
			id -> {
				Optional<QuestionnaireModel> qmTemp = questionnaireModelRepository.findById(id);
				if(questionnaireModelRepository.findById(id).isPresent()) {
					qm.add(qmTemp.get());
		};});
		Campaign campaign = new Campaign(c.getId(), c.getLabel(), qm);
		campaign.setQuestionnaireModels(qm);
		qm.parallelStream().forEach(q -> q.setCampaign(campaign));
		campaignRepository.save(campaign);
		questionnaireModelRepository.saveAll(qm);
	}
	
	public Boolean checkIfQuestionnaireOfCampaignExists(CampaignDto campaign) {
		return !campaign.getQuestionnaireModelsIds().stream().anyMatch(questionaire -> 
			!questionnaireModelRepository.findById(questionaire).isPresent()
			|| questionnaireModelRepository.findById(questionaire).get().getCampaign() != null
		);
	}

}
