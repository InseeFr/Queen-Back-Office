package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.service.dummy.PilotageFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
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
    private InterrogationFakeService interrogationService;
    private PilotageApiComponent pilotageComponent;

    @BeforeEach
    void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        pilotageService = new PilotageFakeService();
        interrogationService = new InterrogationFakeService();
    }

    @Test
    @DisplayName("On check habilitations when ADMIN role do not check pilotage api")
    void testCheckHabilitations02() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.ADMIN));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        pilotageComponent.checkHabilitations(InterrogationFakeService.INTERROGATION1_ID, PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when WEBCLIENT role do not check pilotage api")
    void testCheckHabilitations03() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.WEBCLIENT));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        pilotageComponent.checkHabilitations(InterrogationFakeService.INTERROGATION1_ID, PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations then check pilotage api")
    void testCheckHabilitations04() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER));
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        pilotageComponent.checkHabilitations(InterrogationFakeService.INTERROGATION1_ID, PilotageRole.INTERVIEWER);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isEqualTo(1);
    }

    @Test
    @DisplayName("On check habilitations when pilotage api always return false then throws exception")
    void testCheckHabilitations05() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageService.setHasHabilitation(false);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        assertThatThrownBy(() -> pilotageComponent.checkHabilitations(InterrogationFakeService.INTERROGATION1_ID, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER))
                .isInstanceOf(HabilitationException.class);
        assertThat(pilotageService.getWentThroughHasHabilitation()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("On check if campaign closed return result from pilotage service")
    void testIsClosed(boolean pilotageServiceResult) {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAdminUser();
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageService.setCampaignClosed(pilotageServiceResult);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        boolean isCampaignClosed = pilotageComponent.isClosed("écampaign-id");
        assertThat(isCampaignClosed).isEqualTo(pilotageServiceResult);
    }

    @Test
    @DisplayName("On retrieving interrogations by campaign for current user, return interrogations")
    void testSuByCampaign() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        assertThat(pilotageComponent.getInterrogationsByCampaign("campaign-id")).isEqualTo(pilotageService.getInterrogationSummaries());
    }

    @Test
    @DisplayName("On retrieving campaigns for current interviewer, return campaigns")
    void testInterviewerCampaigns() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getManagerUser();
        authHelper = new AuthenticationFakeHelper(authenticatedUser);
        pilotageComponent = new PilotageApiComponent(pilotageService, authHelper, interrogationService);
        assertThat(pilotageComponent.getInterviewerCampaigns()).isEqualTo(pilotageService.getInterviewerCampaigns());
    }
}
