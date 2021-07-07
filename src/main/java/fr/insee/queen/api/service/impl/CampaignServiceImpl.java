package fr.insee.queen.api.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.campaign.CampaignResponseDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.repository.ApiRepository;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.MetadataRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.SurveyUnitService;

@Service
@Transactional
public class CampaignServiceImpl extends AbstractService<Campaign, String> implements CampaignService {
	
    protected final CampaignRepository campaignRepository;

    protected final QuestionnaireModelRepository questionnaireModelRepository;
    
    protected final MetadataRepository metadataRepository;
    
    @Autowired
	private QuestionnaireModelService questionnaireModelService;

    @Autowired
    private SurveyUnitService surveyUnitService;    
    
    @Autowired
    public CampaignServiceImpl(CampaignRepository campaignRepository, MetadataRepository metadataRepository, QuestionnaireModelRepository questionnaireModelRepository) {
        this.campaignRepository = campaignRepository;
        this.questionnaireModelRepository = questionnaireModelRepository;
        this.metadataRepository = metadataRepository;
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
		
		c.getQuestionnaireIds().stream().forEach(
			id -> {
				Optional<QuestionnaireModel> qmTemp = questionnaireModelRepository.findById(id);
				if(questionnaireModelRepository.findById(id).isPresent()) {
					qm.add(qmTemp.get());
		}});
		Campaign campaign = new Campaign(c.getId().toUpperCase(), c.getLabel(), qm);
		campaign.setQuestionnaireModels(qm);
		qm.parallelStream().forEach(q -> q.setCampaign(campaign));
		campaignRepository.save(campaign);
		questionnaireModelRepository.saveAll(qm);
		//Add Metadata
		if (c.getMetadata()!=null) {
			Metadata m = new Metadata(UUID.randomUUID(), c.getMetadata().getValue(), campaign);
			m = metadataRepository.save(m);
			campaign.setMetadata(m);
			campaignRepository.save(campaign);
		}
		
		// For mongoDB impl
		Set<QuestionnaireModel> qms = campaign.getQuestionnaireModels();
		 if(qms.isEmpty() && !qm.isEmpty()) {
			 qms.addAll(qm);
			 campaignRepository.save(campaign);
		 }
	}
	
	public Boolean checkIfQuestionnaireOfCampaignExists(CampaignDto campaign) {
		return campaign.getQuestionnaireIds().stream().noneMatch(questionaire -> 
			!questionnaireModelRepository.findById(questionaire).isPresent()
			|| questionnaireModelRepository.findById(questionaire).get().getCampaign() != null
		);
	}
	
	public List<CampaignResponseDto> getAllCampaigns() {
		List<Campaign> campaigns = campaignRepository.findAll();
		return campaigns.stream()
				.map(camp -> new CampaignResponseDto(
					camp.getId(), 
					camp.getQuestionnaireModels().stream()
						.map(QuestionnaireModel::getId)
						.collect(Collectors.toList())
					)
				)
				.collect(Collectors.toList());
	}
	
	public List<QuestionnaireIdDto> getQuestionnaireIds(String id) throws NotFoundException {
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (campaignOptional.isPresent()) {
			Campaign campaign = campaignOptional.get();
			return campaign.getQuestionnaireModels().stream()
					.map(q -> new QuestionnaireIdDto(q.getId())).collect(Collectors.toList());
		} else {
			throw new NotFoundException("Campaign " + id + "not found in database");
		}
	}
	
	public List<QuestionnaireModelDto> getQuestionnaireModels(String id) throws NotFoundException {
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (campaignOptional.isPresent()) {
			Campaign campaign = campaignOptional.get();
			return campaign.getQuestionnaireModels().stream()
					.map(q -> new QuestionnaireModelDto(q.getValue())).collect(Collectors.toList());
		} else {
			throw new NotFoundException("Campaign " + id + "not found in database");
		}
	}
	
	@Override
	public void delete(Campaign c) {
		surveyUnitService.findByCampaignId(c.getId()).stream().forEach(su -> surveyUnitService.deleteById(su));
		List<QuestionnaireModel> qmList = questionnaireModelService.findQuestionnaireModelByCampaignId(c.getId());
		if(qmList!=null && !qmList.isEmpty())
		qmList.stream().forEach(qm -> questionnaireModelRepository.delete(qm));
		campaignRepository.delete(c);
	}
}
