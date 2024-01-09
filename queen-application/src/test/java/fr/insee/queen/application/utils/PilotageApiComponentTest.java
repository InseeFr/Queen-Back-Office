package fr.insee.queen.application.utils;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.PilotageApiComponent;
import fr.insee.queen.application.pilotage.service.dummy.PilotageFakeService;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.exception.HabilitationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PilotageApiComponentTest {
    private PilotageFakeService pilotageService;
    private AuthenticationFakeHelper authHelper;
    private AuthenticatedUserTestHelper authenticatedUserTestHelper;
    private SurveyUnitFakeService surveyUnitService;
    private PilotageApiComponent pilotageComponent;

    @BeforeEach
    void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        pilotageService = new PilotageFakeService();
        surveyUnitService = new SurveyUnitFakeService();
    }

    @Test
    @DisplayName("On check habilitations when non authenticated user throw exception")
    void testCheckHabilitations01() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getNotAuthenticatedUser());
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        assertThatThrownBy(() -> pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER))
                .isInstanceOf(HabilitationException.class);
    }

    @Test
    @DisplayName("On check habilitations when ADMIN role do not check pilotage api")
    void testCheckHabilitations02() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.ADMIN));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when WEBCLIENT role do not check pilotage api")
    void testCheckHabilitations03() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.WEBCLIENT));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations then check pilotage api")
    void testCheckHabilitations04() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isEqualTo(1);
    }

    @Test
    @DisplayName("On check habilitations when pilotage api always return false then throws exception")
    void testCheckHabilitations05() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageService.setHasHabilitation(false);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        assertThatThrownBy(() -> pilotageComponent.checkHabilitations("11", PilotageRole.INTERVIEWER, PilotageRole.REVIEWER))
                .isInstanceOf(HabilitationException.class);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("On check if campaign closed return result from pilotage service")
    void testIsClosed(boolean pilotageServiceResult) {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser();
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageService.setCampaignClosed(pilotageServiceResult);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        boolean isCampaignClosed = pilotageComponent.isClosed("écampaign-id");
        assertThat(isCampaignClosed).isEqualTo(pilotageServiceResult);
    }

    @Test
    @DisplayName("On retrieving survey units by campaign for current user, return survey units")
    void testSuByCampaign() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        assertThat(pilotageComponent.getSurveyUnitsByCampaign("campaign-id")).isEqualTo(pilotageService.getSurveyUnitSummaries());
    }

    @Test
    @DisplayName("On retrieving campaigns for current interviewer, return campaigns")
    void testInterviewerCampaigns() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser();
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, surveyUnitService);
        assertThat(pilotageComponent.getInterviewerCampaigns()).isEqualTo(pilotageService.getInterviewerCampaigns());
    }
}
