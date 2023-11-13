package fr.insee.queen.api.surveyunit.service.gateway;

import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

import java.util.List;
import java.util.Optional;

/**
 * CommentRepository is the repository using to access to  Comment table in DB
 *
 * @author Claudel Benjamin
 */
public interface SurveyUnitRepository {
    Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId);

    List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId);

    List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds);

    Optional<SurveyUnit> find(String surveyUnitId);

    Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId);

    Optional<List<String>> findAllIds();

    List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds);

    void deleteSurveyUnits(String campaignId);

    void delete(String surveyUnitId);

    void create(SurveyUnit surveyUnit);

    void updatePersonalization(String surveyUnitId, String personalization);

    void updateComment(String surveyUnitId, String comment);

    void updateData(String surveyUnitId, String data);

    Optional<String> findComment(String surveyUnitId);

    Optional<String> findData(String surveyUnitId);

    Optional<String> findPersonalization(String surveyUnitId);

    boolean exists(String surveyUnitId);

    void updateInfos(SurveyUnit surveyUnit);
}
