package fr.insee.queen.api.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.insee.queen.api.domain.SurveyUnitTempZone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.exception.BadRequestException;

public interface SurveyUnitService extends BaseService<SurveyUnit, String> {

	Optional<SurveyUnit> findById(String id);

	SurveyUnitDto findDtoById(String id);

	List<SurveyUnitDto> findDtoByCampaignId(String id);

	void save(SurveyUnit newSU);

	List<SurveyUnit> findByCampaignId(String id);

	List<SurveyUnit> findAll();

	void updateSurveyUnit(SurveyUnit su, JsonNode surveyUnit);

	void updateSurveyUnitImproved(String id, JsonNode surveyUnit);

	public void generateDepositProof(SurveyUnit su, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	public Collection<SurveyUnitResponseDto> getSurveyUnitsByCampaign(String id, HttpServletRequest request) throws BadRequestException;

	void createSurveyUnit(SurveyUnitResponseDto su, Campaign campaign, QuestionnaireModel questionnaireModel);

	HttpStatus postSurveyUnit(String id, SurveyUnitResponseDto su);
	ResponseEntity<String> postSurveyUnitImproved(String id, SurveyUnitResponseDto su);

	Iterable<SurveyUnit> findByIds(List<String> lstSurveyUnitId);
	
	void delete(SurveyUnit su);

	void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit);

	List<SurveyUnitTempZone> getAllSurveyUnitTempZone();
}
