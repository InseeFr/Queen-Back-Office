package fr.insee.queen.api.surveyunit.controller;

import fr.insee.queen.api.pilotage.controller.dummy.HabilitationFakeComponent;
import fr.insee.queen.api.pilotage.service.dummy.PilotageFakeService;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitByCampaignDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitDto;
import fr.insee.queen.api.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitControllerTest {

    private SurveyUnitController surveyUnitController;
    private PilotageFakeService pilotageService;
    private SurveyUnitFakeService surveyUnitService;
    private HabilitationFakeComponent habilitationComponent;
    private AuthenticationFakeHelper authenticationHelper;
    private Authentication authenticatedUser;

    @BeforeEach
    public void init() {
        AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser();
        authenticationHelper = new AuthenticationFakeHelper(authenticatedUser);
        habilitationComponent = new HabilitationFakeComponent();
        surveyUnitService = new SurveyUnitFakeService();
        pilotageService = new PilotageFakeService();
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when integration override is true then return all survey units for this campaign")
    void testGetSurveyUnitsCampaign() {
        surveyUnitController = new SurveyUnitController("true", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitByCampaignDto> surveyUnits = surveyUnitController.getListSurveyUnitByCampaign("campaign-id");
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when integration override is false then return survey units from pilotage api")
    void testGetSurveyUnitsCampaign01() {
        surveyUnitController = new SurveyUnitController("false", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitByCampaignDto> surveyUnits = surveyUnitController.getListSurveyUnitByCampaign("campaign-id");
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeService.SURVEY_UNIT1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when survey units are empty then throws exception")
    void testGetSurveyUnitsCampaign02() {
        pilotageService.hasEmptySurveyUnits(true);
        surveyUnitController = new SurveyUnitController("false", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        assertThatThrownBy(() -> surveyUnitController.getListSurveyUnitByCampaign("campaign-id"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("On retrieving survey units for an interviewer, return survey units found")
    void testGetSurveyUnitsForInterviewers03() {
        surveyUnitController = new SurveyUnitController("false", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitDto> surveyUnits =  surveyUnitController.getInterviewerSurveyUnits();
        assertThat(surveyUnits).size().isEqualTo(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnits.get(1).id()).isEqualTo(PilotageFakeService.SURVEY_UNIT2_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for an interviewer, return survey units found")
    void testGetSurveyUnitsForInterviewers04() {
        surveyUnitController = new SurveyUnitController("true", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitDto> surveyUnits =  surveyUnitController.getInterviewerSurveyUnits();
        assertThat(surveyUnits).size().isEqualTo(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnits.get(1).id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT2_ID);
    }
}
