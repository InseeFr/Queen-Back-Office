package fr.insee.queen.api.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

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

	void updateSurveyUnit(SurveyUnit su, JsonNode surveyUnit);
	void updateSurveyUnitImproved(String id, JsonNode surveyUnit);

	public void generateDepositProof(SurveyUnit su, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	public Collection<SurveyUnitResponseDto> getSurveyUnitsByCampaign(String id, HttpServletRequest request) throws BadRequestException;

	void createSurveyUnit(SurveyUnitResponseDto su, Campaign campaign, QuestionnaireModel questionnaireModel);
	HttpStatus postSurveyUnit(String id, SurveyUnitResponseDto su);

	Iterable<SurveyUnit> findByIds(List<String> lstSurveyUnitId);
	
	void deleteById(SurveyUnit su);
}
