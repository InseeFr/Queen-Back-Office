package fr.insee.queen.api.utils;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.pilotage.controller.HabilitationApiComponent;
import fr.insee.queen.api.pilotage.controller.HabilitationComponent;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.dummy.PilotageFakeService;
import fr.insee.queen.api.pilotage.service.exception.HabilitationException;
import fr.insee.queen.api.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.utils.dummy.AuthenticationFakeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabilitationComponentTest {
    private PilotageFakeService pilotageService;
    private ApplicationProperties applicationProperties;
    private AuthenticationFakeHelper authHelper;
    private AuthenticatedUserTestHelper authenticatedUserTestHelper;
    private SurveyUnitFakeService surveyUnitService;
    private HabilitationComponent habilitationComponent;

    @BeforeEach
    void init() {
        authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
        pilotageService = new PilotageFakeService();
        surveyUnitService = new SurveyUnitFakeService();
        applicationProperties = new ApplicationProperties(null, null, null, null, null, null, AuthEnumProperties.OIDC);
    }

    @Test
    @DisplayName("On check habilitations when integration is true do not check pilotage api")
    void testCheckHabilitations01() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser());
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "true");
        habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when NOAUTH mode do not check pilotage api")
    void testCheckHabilitations02() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser());
        applicationProperties = new ApplicationProperties(null, null, null, null, null, null, AuthEnumProperties.NOAUTH);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when non authenticated user throw exception")
    void testCheckHabilitations03() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getNotAuthenticatedUser());
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        assertThatThrownBy(() -> habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER))
                .isInstanceOf(HabilitationException.class);
    }

    @Test
    @DisplayName("On check habilitations when ADMIN role do not check pilotage api")
    void testCheckHabilitations04() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.ADMIN));
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when WEBCLIENT role do not check pilotage api")
    void testCheckHabilitations05() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.WEBCLIENT));
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations then check pilotage api")
    void testCheckHabilitations06() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER));
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isEqualTo(1);
    }

    @Test
    @DisplayName("On check habilitations when pilotage api always return false then throws exception")
    void testCheckHabilitations07() {
        authHelper = new AuthenticationFakeHelper(authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER));
        pilotageService.hasHabilitation(false);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        assertThatThrownBy(() -> habilitationComponent.checkHabilitations("11", PilotageRole.INTERVIEWER, PilotageRole.REVIEWER))
                .isInstanceOf(HabilitationException.class);
        assertThat(pilotageService.wentThroughHasHabilitation()).isEqualTo(2);
    }
}
