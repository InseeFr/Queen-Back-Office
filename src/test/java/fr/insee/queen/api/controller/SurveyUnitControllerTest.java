package fr.insee.queen.api.controller;

import fr.insee.queen.api.controller.dummy.HabilitationFakeComponent;
import fr.insee.queen.api.controller.surveyunit.SurveyUnitController;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.service.dummy.PilotageFakeService;
import fr.insee.queen.api.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.dummy.AuthenticationFakeHelper;
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
    private AuthenticatedUserTestHelper authenticatedUserTestHelper;
    private AuthenticationFakeHelper authenticationHelper;
    private Authentication authenticatedUser;

    @BeforeEach
    public void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser();
        authenticationHelper = new AuthenticationFakeHelper();
        habilitationComponent = new HabilitationFakeComponent();
        surveyUnitService = new SurveyUnitFakeService();
        pilotageService = new PilotageFakeService();
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when integration override is true then return all survey units for this campaign")
    void testGetSurveyUnitsCampaign() {
        surveyUnitController = new SurveyUnitController("true", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitSummaryDto> surveyUnits =  surveyUnitController.getListSurveyUnitByCampaign("campaign-id", authenticatedUser);
        assertThat(surveyUnits).hasSize(3);
        assertThat(surveyUnits.get(0).id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when integration override is false then return survey units from pilotage api")
    void testGetSurveyUnitsCampaign01() {
        surveyUnitController = new SurveyUnitController("false", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        List<SurveyUnitSummaryDto> surveyUnits =  surveyUnitController.getListSurveyUnitByCampaign("campaign-id", authenticatedUser);
        assertThat(surveyUnits).hasSize(2);
        assertThat(surveyUnits.get(0).id()).isEqualTo(PilotageFakeService.SURVEY_UNIT1_ID);
    }

    @Test
    @DisplayName("On retrieving survey units for a campaign, when survey units are empty then throws exception")
    void testGetSurveyUnitsCampaign02() {
        pilotageService.hasEmptySurveyUnits(true);
        surveyUnitController = new SurveyUnitController("false", surveyUnitService, pilotageService, habilitationComponent, authenticationHelper);
        assertThatThrownBy(() -> surveyUnitController.getListSurveyUnitByCampaign("campaign-id", authenticatedUser))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
