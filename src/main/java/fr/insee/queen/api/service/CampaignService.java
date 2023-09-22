package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.exception.CampaignCreationException;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CampaignService {
	private final ParadataEventRepository paradataEventRepository;
	private final StateDataRepository stateDataRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignRepository campaignRepository;
	private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
	private final QuestionnaireModelService questionnaireModelService;

	public Campaign getCampaign(String campaignId) {
		return campaignRepository.findById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s was not found", campaignId)));
	}

	public boolean existsById(String campaignId) {
		return campaignRepository.existsById(campaignId);
	}

	public List<CampaignSummaryDto> getAllCampaigns() {
		List<Campaign> campaigns = campaignRepository.findAll();
		return campaigns.stream()
				.map(camp -> new CampaignSummaryDto(
						camp.id(),
						questionnaireModelService.findAllQuestionnaireIdDtoByCampaignId(camp.id())))
				.toList();
	}

	public List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId) {
		Campaign campaign = getCampaign(campaignId);
		return campaign.questionnaireModels().stream()
				.map(q -> new QuestionnaireModelIdDto(q.id())).toList();
	}

	public List<QuestionnaireModelDto> getQuestionnaireModels(String campaignId) {
		Campaign campaign = getCampaign(campaignId);
		return campaign.questionnaireModels().stream()
				.map(q -> new QuestionnaireModelDto(q.value())).toList();
	}

	public void delete(String campaignId) {
		if(!campaignRepository.existsById(campaignId)) {
			throw new EntityNotFoundException(String.format("Campaign %s does not exist, unable to delete", campaignId));
		}
		paradataEventRepository.deleteParadataEvents(campaignId);
		stateDataRepository.deleteStateDatas(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		surveyUnitRepository.deleteSurveyUnits(campaignId);

		List<QuestionnaireModel> qmList = questionnaireModelService.findQuestionnaireModelByCampaignId(campaignId);
		if(qmList!=null && !qmList.isEmpty()) {
			questionnaireModelRepository.deleteAll(qmList);
		}
		campaignRepository.deleteById(campaignId);
	}

	public void createCampaign(CampaignInputDto campaignInputDto) {
		String campaignId = campaignInputDto.id().toUpperCase();

		if (existsById(campaignId)) {
			throw new CampaignCreationException(String.format("Campaign %s already exists. Creation aborted", campaignId));
		}

		List<String> questionnaireIds = campaignInputDto.questionnaireIds().stream().toList();
		List<QuestionnaireModel> questionnaireModels = questionnaireModelService.findByIds(questionnaireIds);

		if(questionnaireIds.size() != questionnaireModels.size()) {
			throw new CampaignCreationException(
					String.format("One or more questionnaires do not exist for campaign %s. Creation aborted.", campaignId));
		}
		// check that questionnaire models exist and are not already associated with a campaign
		boolean canQuestionnairesBeAssociated = questionnaireModels.stream()
				.allMatch(questionnaireModel -> questionnaireModel.campaign() != null);

		if(!canQuestionnairesBeAssociated) {
			throw new CampaignCreationException(
					String.format("One or more questionnaires are already associated for campaign %s. Creation aborted.", campaignId));
		}

		Campaign campaign = new Campaign(campaignId, campaignInputDto.label(), new HashSet<>(questionnaireModels));
		questionnaireModels.parallelStream()
				.forEach(questionnaireModel -> questionnaireModel.campaign(campaign));

		if (campaignInputDto.metadata() != null) {
			Metadata m = new Metadata(UUID.randomUUID(), campaignInputDto.metadata().value().toString(), campaign);
			campaign.metadata(m);
		}
		campaignRepository.save(campaign);
	}
}
