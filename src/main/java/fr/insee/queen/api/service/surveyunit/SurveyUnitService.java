package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.dto.depositproof.PdfDepositProof;
import fr.insee.queen.api.dto.input.SurveyUnitCreateInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitUpdateInputDto;
import fr.insee.queen.api.dto.surveyunit.*;

import java.util.List;
import java.util.Optional;

public interface SurveyUnitService {
	boolean existsById(String surveyUnitId);

	void checkExistence(String surveyUnitId);
	SurveyUnitDto getSurveyUnit(String id);
	List<SurveyUnitSummaryDto> findByCampaignId(String campaignId);
	List<String> findAllSurveyUnitIds();
	void updateSurveyUnit(String surveyUnitId, SurveyUnitUpdateInputDto surveyUnit);
	PdfDepositProof generateDepositProof(String userId, String surveyUnitId);
	void createSurveyUnit(String campaignId, SurveyUnitCreateInputDto surveyUnit);
	List<SurveyUnitSummaryDto> findSummaryByIds(List<String> surveyUnits);
	Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId);
	List<SurveyUnitWithStateDto> findWithStateByIds(List<String> surveyUnits);
	void delete(String surveyUnitId);
	SurveyUnitDepositProofDto getSurveyUnitDepositProof(String surveyUnitId);
	SurveyUnitHabilitationDto getSurveyUnitWithCampaignById(String surveyUnitId);
}
