package fr.insee.queen.api.utils;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.controller.utils.HabilitationApiComponent;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.service.dummy.PilotageFakeService;
import fr.insee.queen.api.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.api.service.exception.HabilitationException;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import fr.insee.queen.api.utils.dummy.AuthenticationFakeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

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
        authHelper = new AuthenticationFakeHelper();
        surveyUnitService = new SurveyUnitFakeService();
        applicationProperties = new ApplicationProperties(null, null, null, null, null, AuthEnumProperties.KEYCLOAK);
    }

    @Test
    @DisplayName("On check habilitations when integration is true do not check pilotage api")
    void testCheckHabilitations01() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "true");
        habilitationComponent.checkHabilitations(authenticatedUserTestHelper.getAuthenticatedUser(), "11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when NOAUTH mode do not check pilotage api")
    void testCheckHabilitations02() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser();
        applicationProperties = new ApplicationProperties(null, null, null, null, null, AuthEnumProperties.NOAUTH);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations(authenticatedUser, "11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when non authenticated user throw exception")
    void testCheckHabilitations03() {
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        Authentication nonAuthenticatedUser = authenticatedUserTestHelper.getNotAuthenticatedUser();
        assertThatThrownBy(() -> habilitationComponent.checkHabilitations(nonAuthenticatedUser, "11", PilotageRole.INTERVIEWER))
                .isInstanceOf(HabilitationException.class);
    }

    @Test
    @DisplayName("On check habilitations when ADMIN role do not check pilotage api")
    void testCheckHabilitations04() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.ADMIN);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations(authenticatedUser, "11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations when WEBCLIENT role do not check pilotage api")
    void testCheckHabilitations05() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.WEBCLIENT);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations(authenticatedUser, "11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isZero();
    }

    @Test
    @DisplayName("On check habilitations then check pilotage api")
    void testCheckHabilitations06() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        habilitationComponent.checkHabilitations(authenticatedUser, "11", PilotageRole.INTERVIEWER);
        assertThat(pilotageService.wentThroughHasHabilitation()).isEqualTo(1);
    }

    @Test
    @DisplayName("On check habilitations when pilotage api always return false then throws exception")
    void testCheckHabilitations07() {
        Authentication authenticatedUser = authenticatedUserTestHelper.getAuthenticatedUser(
                AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER);
        pilotageService.hasHabilitation(false);
        habilitationComponent = new HabilitationApiComponent(pilotageService, applicationProperties, authHelper, surveyUnitService, "false");
        assertThatThrownBy(() -> habilitationComponent.checkHabilitations(authenticatedUser, "11", PilotageRole.INTERVIEWER, PilotageRole.REVIEWER))
                .isInstanceOf(HabilitationException.class);
        assertThat(pilotageService.wentThroughHasHabilitation()).isEqualTo(2);
    }
}
