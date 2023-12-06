package fr.insee.queen.api.pilotage.controller;

import fr.insee.queen.api.campaign.service.CampaignService;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnExpression(value = "'${application.auth}' == 'NOAUTH' or ${feature.enable.pilotage} == false")
@RequiredArgsConstructor
@Component
public class NoPilotageComponent implements PilotageComponent {
    private final SurveyUnitService surveyUnitService;
    private final CampaignService campaignService;

    @Override
    public boolean isClosed(String campaignId) {
        return true;
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId) {
        return surveyUnitService.findSummariesByCampaignId(campaignId);
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        return campaignService.getAllCampaigns().stream()
                .map(campaign -> new PilotageCampaign(campaign.getId(), campaign.getQuestionnaireIds().stream().toList()))
                .toList();
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits() {
        return surveyUnitService.findAllSurveyUnits();
    }

    @Override
    public void checkHabilitations(String surveyUnitId, PilotageRole... roles) {
        surveyUnitService.throwExceptionIfSurveyUnitNotExist(surveyUnitId);
    }
}
