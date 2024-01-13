package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

import java.util.List;
import java.util.Optional;

public interface SurveyUnitService {
    boolean existsById(String surveyUnitId);

    void throwExceptionIfSurveyUnitNotExist(String surveyUnitId);

    void throwExceptionIfSurveyUnitExist(String surveyUnitId);

    SurveyUnit getSurveyUnit(String id);

    List<SurveyUnitSummary> findSummariesByCampaignId(String campaignId);

    List<String> findAllSurveyUnitIds();

    void updateSurveyUnit(SurveyUnit surveyUnit);

    void createSurveyUnit(SurveyUnit surveyUnit) throws StateDataInvalidDateException;

    List<SurveyUnitSummary> findSummariesByIds(List<String> surveyUnits);

    Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId);

    List<SurveyUnitState> findWithStateByIds(List<String> surveyUnits);

    void delete(String surveyUnitId);

    SurveyUnitDepositProof getSurveyUnitDepositProof(String surveyUnitId);

    SurveyUnitSummary getSurveyUnitWithCampaignById(String surveyUnitId);

    List<SurveyUnit> findByIds(List<String> surveyUnitIds);

    List<SurveyUnit> findAllSurveyUnits();
}
