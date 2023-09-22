package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.domain.SurveyUnitTempZone;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitInputDto;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.exception.DepositProofException;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.pdfutils.ExportPdf;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.StateDataRepository;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class SurveyUnitService {
	private final SurveyUnitRepository surveyUnitRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignRepository campaignRepository;
	private final StateDataRepository stateDataRepository;

	public boolean existsById(String surveyUnitId) {
		return surveyUnitRepository.existsById(surveyUnitId);
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

	public void updateSurveyUnit(String surveyUnitId, SurveyUnitInputDto surveyUnit) {
		if(!surveyUnitRepository.existsById(surveyUnitId)) {
			throw new EntityNotFoundException(String.format("Survey unit id %s not found", surveyUnitId));
		}
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
			StateDataInputDto stateData = surveyUnit.stateData();
			stateDataRepository.updateStateData(surveyUnitId, stateData.date(), stateData.currentPage(), stateData.state());
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

	public void createSurveyUnit(String campaignId, SurveyUnitInputDto surveyUnit) {
		if(!campaignRepository.existsById(campaignId)) {
			throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
		}

		String surveyUnitId = surveyUnit.id();
		surveyUnitRepository.createSurveyUnit(surveyUnit.id(), campaignId,
				surveyUnit.questionnaireId(),
				surveyUnit.data().toString(),
				surveyUnit.comment().toString(),
				surveyUnit.personalization().toString());

		SurveyUnit su = surveyUnitRepository.findById(surveyUnitId)
						.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s should have been created, but is not", surveyUnitId)));

		StateData stateData = new StateData();
		StateDataInputDto stateDataInput = surveyUnit.stateData();
		if(stateDataInput != null) {
			stateData = new StateData(UUID.randomUUID(), stateDataInput.state(), stateDataInput.date(), stateDataInput.currentPage(), su);
		}
		stateDataRepository.save(stateData);
	}

	public List<SurveyUnitSummaryDto> findSummaryByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllSummaryByIdIn(surveyUnits);
	}

	public List<SurveyUnitWithStateDto> findWithStateByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllWithStateByIdIn(surveyUnits);
	}
	
	public void delete(String surveyUnitId) {
		if(!surveyUnitRepository.existsById(surveyUnitId)) {
			throw new EntityNotFoundException(String.format("Survey unit id %s not found", surveyUnitId));
		}
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
}
