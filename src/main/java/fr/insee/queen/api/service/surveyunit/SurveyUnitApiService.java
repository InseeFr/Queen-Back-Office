package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.dto.depositproof.PdfDepositProof;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitInputDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.statedata.StateDataType;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import fr.insee.queen.api.repository.SurveyUnitTempZoneRepository;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.depositproof.PDFDepositProofService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitApiService implements SurveyUnitService {
	private final SurveyUnitRepository surveyUnitRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignExistenceService campaignExistenceService;
	private final PDFDepositProofService pdfService;

	@Override
	public boolean existsById(String surveyUnitId) {
		return surveyUnitRepository.existsById(surveyUnitId);
	}

	@Override
	public void checkExistence(String surveyUnitId) {
		if(!existsById(surveyUnitId)) {
			throw new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId));
		}
	}

	@Override
	public SurveyUnitDto getSurveyUnit(String id) {
		return surveyUnitRepository.findOneById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", id)));
	}

	@Override
	public List<SurveyUnitSummaryDto> findByCampaignId(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return surveyUnitRepository.findAllSummaryByCampaignId(campaignId);
	}

	@Override
	public List<String> findAllSurveyUnitIds() {
		return surveyUnitRepository.findAllIds()
				.orElseThrow(() -> new EntityNotFoundException("List of survey unit ids not found"));
	}

	@Transactional
	@Override
	public void updateSurveyUnit(String surveyUnitId, SurveyUnitInputDto surveyUnit) {
		checkExistence(surveyUnitId);

		if(surveyUnit.personalization() != null) {
			surveyUnitRepository.updatePersonalization(surveyUnitId,surveyUnit.personalization().toString());
		}
		if(surveyUnit.comment() != null) {
			surveyUnitRepository.updateComment(surveyUnitId,surveyUnit.comment().toString());
		}
		if(surveyUnit.data() != null) {
			surveyUnitRepository.updateData(surveyUnitId,surveyUnit.data().toString());
		}
		if(surveyUnit.stateData() != null) {
			surveyUnitRepository.updateStateData(surveyUnitId, StateDataInputDto.toModel(surveyUnit.stateData()));
		}
	}

	@Override
	public PdfDepositProof generateDepositProof(String userId, String surveyUnitId) {
		SurveyUnitDepositProofDto surveyUnit = getSurveyUnitDepositProof(surveyUnitId);
		String campaignId = surveyUnit.campaign().id();
		String campaignLabel = surveyUnit.campaign().label();
		String date = "";

		if (surveyUnit.stateData() == null) {
			throw new EntityNotFoundException(String.format("State data for survey unit %s was not found", surveyUnitId));
		}

		if(Arrays.asList(StateDataType.EXTRACTED,StateDataType.VALIDATED).contains(surveyUnit.stateData().state())) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm"); 
			date = dateFormat.format(new Date(surveyUnit.stateData().date()));
		}
		String filename = String.format("%s_%s.pdf", campaignId, userId);

		return new PdfDepositProof(filename, pdfService.retrievePdf(date,campaignLabel, userId));
	}

	@Transactional
	@Override
	public void createSurveyUnit(String campaignId, SurveyUnitInputDto surveyUnit) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);

		StateDataDto stateData = StateDataDto.createEmptyStateData();
		if(surveyUnit.stateData() != null) {
			stateData = StateDataInputDto.toModel(surveyUnit.stateData());
		}

		surveyUnitRepository.createSurveyUnit(surveyUnit.id(), campaignId,
				surveyUnit.questionnaireId(),
				surveyUnit.data().toString(),
				surveyUnit.comment().toString(),
				surveyUnit.personalization().toString(),
				stateData);
	}

	@Override
	public List<SurveyUnitSummaryDto> findSummaryByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllSummaryByIdIn(surveyUnits);
	}

	@Override
	public Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId) {
		return surveyUnitRepository.findSummaryById(surveyUnitId);
	}

	@Override
	public List<SurveyUnitWithStateDto> findWithStateByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllWithStateByIdIn(surveyUnits);
	}

	@Transactional
	public void delete(String surveyUnitId) {
		checkExistence(surveyUnitId);
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
		surveyUnitRepository.deleteById(surveyUnitId);
	}

	@Override
	public SurveyUnitDepositProofDto getSurveyUnitDepositProof(String surveyUnitId) {
		return surveyUnitRepository
				.findWithCampaignAndStateById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}

	@Override
	public SurveyUnitHabilitationDto getSurveyUnitWithCampaignById(String surveyUnitId) {
		return surveyUnitRepository.findWithCampaignById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}
}
