package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.campaign.dto.output.CampaignSummaryDto;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitByCampaignDto;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewerControllerTest {
    private PilotageFakeComponent pilotageComponent;
    private InterviewerController interviewerController;

    @BeforeEach
    void init() {
        pilotageComponent = new PilotageFakeComponent();
        interviewerController = new InterviewerController(pilotageComponent);
    }

    @Test
    @DisplayName("On retrieving interviewer campaigns, all interviewer campaigns are retrieved")
    void testGetInterviewerCampaigns01() {
        List<CampaignSummaryDto> campaigns = interviewerController.getInterviewerCampaignList("userId");
        assertThat(pilotageComponent.isWentThroughInterviewerCampaigns()).isTrue();
        assertThat(campaigns).hasSize(2);
        assertThat(campaigns.get(0).getId()).isEqualTo(PilotageFakeComponent.CAMPAIGN1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, then return survey units from pilotage service")
    void testGetSurveyUnitsCampaign01() {
        List<SurveyUnitByCampaignDto> surveyUnits = interviewerController.getListSurveyUnitByCampaign("campaign-id");
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeComponent.SURVEY_UNIT1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when survey units are empty then throws exception")
    void testGetSurveyUnitsCampaign02() {
        pilotageComponent.setHasEmptySurveyUnits(true);
        assertThatThrownBy(() -> interviewerController.getListSurveyUnitByCampaign("campaign-id"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("On retrieving survey units for an interviewer, return survey units found")
    void testGetSurveyUnitsForInterviewers03() {
        List<SurveyUnitDto> surveyUnits =  interviewerController.getInterviewerSurveyUnits();
        assertThat(surveyUnits).size().isEqualTo(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeComponent.SURVEY_UNIT1_ID);
        assertThat(surveyUnits.get(1).id()).isEqualTo(PilotageFakeComponent.SURVEY_UNIT2_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for an interviewer, return survey units found")
    void testGetSurveyUnitsForInterviewers04() {
        List<SurveyUnitDto> surveyUnits =  interviewerController.getInterviewerSurveyUnits();
        assertThat(surveyUnits).size().isEqualTo(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeComponent.SURVEY_UNIT1_ID);
        assertThat(surveyUnits.get(1).id()).isEqualTo(PilotageFakeComponent.SURVEY_UNIT2_ID);
    }
}
