package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.domain.SurveyUnitTempZone;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitInputDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.exception.DepositProofException;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.pdfutils.ExportPdf;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import fr.insee.queen.api.repository.SurveyUnitTempZoneRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitService {
	private final SurveyUnitRepository surveyUnitRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignService campaignService;

	public boolean existsById(String surveyUnitId) {
		return surveyUnitRepository.existsById(surveyUnitId);
	}

	public void checkExistence(String surveyUnitId) {
		if(!existsById(surveyUnitId)) {
			throw new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId));
		}
	}

	public SurveyUnitDto getSurveyUnit(String id) {
		return surveyUnitRepository.findOneById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", id)));
	}

	public List<SurveyUnitSummaryDto> findByCampaignId(String id) {
		return surveyUnitRepository.findAllSummaryByCampaignId(id);
	}

	public List<String> findAllSurveyUnitIds() {
		return surveyUnitRepository.findAllIds()
				.orElseThrow(() -> new EntityNotFoundException("List of survey unit ids not found"));
	}

	@Transactional
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

	public void generateDepositProof(String userId, SurveyUnitDepositProofDto su, HttpServletResponse response) {
		String campaignId = su.campaign().id();
		String campaignLabel = su.campaign().label();
		String date = "";
		if(Arrays.asList(StateDataType.EXTRACTED,StateDataType.VALIDATED).contains(su.stateData().state())) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm"); 
			date = dateFormat.format(new Date(su.stateData().date()));
		}
		ExportPdf exp = new ExportPdf();
		try {
			exp.doExport(response, date, campaignId, campaignLabel, userId);
		} catch (ServletException | IOException e) {
			log.error(e.getMessage(), e);
			throw new DepositProofException("ERROR Export: " + e.getMessage());
        }
	}

	@Transactional
	public void createSurveyUnit(String campaignId, SurveyUnitInputDto surveyUnit) {
		campaignService.checkExistence(campaignId);

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

	public List<SurveyUnitSummaryDto> findSummaryByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllSummaryByIdIn(surveyUnits);
	}

	public Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId) {
		return surveyUnitRepository.findSummaryById(surveyUnitId);
	}

	public List<SurveyUnitWithStateDto> findWithStateByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllWithStateByIdIn(surveyUnits);
	}

	@Transactional
	public void delete(String surveyUnitId) {
		checkExistence(surveyUnitId);
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
		surveyUnitRepository.deleteById(surveyUnitId);
	}

	public void saveSurveyUnitToTempZone(String surveyUnitId, String userId, JsonNode surveyUnit){
    	Long date = new Date().getTime();
		SurveyUnitTempZone surveyUnitTempZoneToSave = new SurveyUnitTempZone(surveyUnitId,userId,date,surveyUnit.toString());
    	surveyUnitTempZoneRepository.save(surveyUnitTempZoneToSave);
	}

	public List<SurveyUnitTempZoneDto> getAllSurveyUnitTempZoneDto(){
    	return surveyUnitTempZoneRepository.findAllProjectedBy();
	}

	public SurveyUnitDepositProofDto getSurveyUnitDepositProof(String surveyUnitId) {
		return surveyUnitRepository
				.findWithCampaignAndStateById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}

	public SurveyUnitHabilitationDto getSurveyUnitWithCampaignById(String surveyUnitId) {
		return surveyUnitRepository.findWithCampaignById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}
}
