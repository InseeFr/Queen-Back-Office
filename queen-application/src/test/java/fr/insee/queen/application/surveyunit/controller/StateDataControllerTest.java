package fr.insee.queen.application.surveyunit.controller;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.controller.exception.LockedResourceException;
import fr.insee.queen.application.surveyunit.dto.input.StateDataInput;
import fr.insee.queen.application.surveyunit.dto.input.StateDataTypeInput;
import fr.insee.queen.application.surveyunit.service.dummy.StateDataFakeService;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateDataControllerTest {

    private StateDataController stateDataController;
    private PilotageFakeComponent pilotageFakeComponent;
    private SurveyUnitFakeService surveyUnitFakeService;
    private StateDataFakeService stateDataFakeService;
    private AuthenticationFakeHelper authenticationFakeHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        pilotageFakeComponent = new PilotageFakeComponent();
        surveyUnitFakeService = new SurveyUnitFakeService();
        stateDataFakeService = new StateDataFakeService();
        authenticationFakeHelper = new AuthenticationFakeHelper();
        stateDataController = new StateDataController(stateDataFakeService, surveyUnitFakeService, pilotageFakeComponent, authenticationFakeHelper);
    }


    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateStateDataException() {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> stateDataController.setStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID, stateDataInput))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update data when campaign is sensitive and role is admin/webclient")
    void testUpdateStateData04() throws StateDataInvalidDateException, LockedResourceException {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        stateDataController.setStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update data when campaign is sensitive and role is interviewer/survey-unit")
    void testUpdateStateData05(Authentication auth) throws StateDataInvalidDateException, LockedResourceException {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        stateDataController.setStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @Test
    @DisplayName("Should update data when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testUpdateStateData06() throws StateDataInvalidDateException, LockedResourceException {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(stateData.state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        stateDataController.setStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @ParameterizedTest
    @CsvSource(value = {SurveyUnitFakeService.SURVEY_UNIT4_ID, SurveyUnitFakeService.SURVEY_UNIT5_ID})
    @DisplayName("Should update data when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateStateDataException02(String surveyUnitId) {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(surveyUnitId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> stateDataController.setStateData(surveyUnitId, stateDataInput))
                .isInstanceOf(LockedResourceException.class);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    private static Stream<Arguments> provideInterviewerAndSuUsers() {
        AuthenticatedUserTestHelper provider = new AuthenticatedUserTestHelper();
        return Stream.of(
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT)));
    }

    private static Stream<Arguments> provideAdminAndWebclientUsers() {
        AuthenticatedUserTestHelper provider = new AuthenticatedUserTestHelper();
        return Stream.of(
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.WEBCLIENT)),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.ADMIN)));
    }
}
