package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.controller.exception.LockedResourceException;
import fr.insee.queen.application.surveyunit.service.dummy.DataFakeService;
import fr.insee.queen.application.surveyunit.service.dummy.StateDataFakeService;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
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

class DataControllerTest {

    private DataController dataController;
    private PilotageFakeComponent pilotageFakeComponent;
    private SurveyUnitFakeService surveyUnitFakeService;
    private StateDataFakeService stateDataFakeService;
    private DataFakeService dataFakeService;
    private AuthenticationFakeHelper authenticationFakeHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        pilotageFakeComponent = new PilotageFakeComponent();
        surveyUnitFakeService = new SurveyUnitFakeService();
        stateDataFakeService = new StateDataFakeService();
        dataFakeService = new DataFakeService();
        authenticationFakeHelper = new AuthenticationFakeHelper();
        dataController = new DataController(dataFakeService, pilotageFakeComponent, stateDataFakeService, surveyUnitFakeService, authenticationFakeHelper);
    }
    
    
    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateSurveyUnitException() {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> dataController.updateData(dataInput, SurveyUnitFakeService.SURVEY_UNIT3_ID))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update data when campaign is sensitive and role is admin/webclient")
    void testUpdateSurveyUnit04() throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        dataController.updateData(dataInput, SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update data when campaign is sensitive and role is interviewer/survey-unit")
    void testUpdateSurveyUnit05(Authentication auth) throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        dataController.updateData(dataInput, SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @Test
    @DisplayName("Should update data when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testUpdateSurveyUnit06() throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(stateData.state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        dataController.updateData(dataInput, SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {SurveyUnitFakeService.SURVEY_UNIT4_ID, SurveyUnitFakeService.SURVEY_UNIT5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateSurveyUnitException02(String surveyUnitId) {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(surveyUnitId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when

        assertThatThrownBy(() -> dataController.updateData(dataInput, surveyUnitId))
                .isInstanceOf(LockedResourceException.class);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isFalse();
    }

    @Test
    @DisplayName("Should return data when campaign is not sensitive")
    void testGetSurveyUnit01() {
        // given
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);
        
        // when
        ObjectNode data = dataController.getDataBySurveyUnit(SurveyUnitFakeService.SURVEY_UNIT1_ID);

        // then
        ObjectNode surveyUnitData = dataFakeService.getData(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(data).isEqualTo(surveyUnitData);
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testGetSurveyUnitException() {
        // given
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> dataController.getDataBySurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should return data when campaign is sensitive and role is admin/webclient")
    void testGetSurveyUnit02() {
        // given
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        ObjectNode data = dataController.getDataBySurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        ObjectNode surveyUnitData = dataFakeService.getData(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(data).isEqualTo(surveyUnitData);
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return data when campaign is sensitive and role is interviewer/survey-unit")
    void testGetSurveyUnit03(Authentication auth) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        ObjectNode data = dataController.getDataBySurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        ObjectNode surveyUnitData = dataFakeService.getData(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data).isEqualTo(surveyUnitData);
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsersWithExtractedOrValidatedStates")
    @DisplayName("Should return empty data when campaign is sensitive, role is interviewer/survey-unit and state is EXTRACTED/VALIDATED")
    void testGetSurveyUnit04(Authentication auth, String surveyUnitId) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        ObjectNode surveyUnitData = dataFakeService.getData(surveyUnitId);
        StateData stateData = stateDataFakeService.getStateData(surveyUnitId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        ObjectNode data = dataController.getDataBySurveyUnit(surveyUnitId);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data)
                .isNotEqualTo(surveyUnitData)
                .isEqualTo(JsonNodeFactory.instance.objectNode());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return data when campaign is sensitive, role is interviewer/survey-unit, and state data is null")
    void testGetSurveyUnit05(Authentication auth) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT6_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        ObjectNode surveyUnitData = dataFakeService.getData(SurveyUnitFakeService.SURVEY_UNIT6_ID);
        StateData stateData = stateDataFakeService.getStateData(SurveyUnitFakeService.SURVEY_UNIT6_ID);
        assertThat(stateData).isNull();

        // when
        ObjectNode data = dataController.getDataBySurveyUnit(SurveyUnitFakeService.SURVEY_UNIT6_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data).isEqualTo(surveyUnitData);
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

    private static Stream<Arguments> provideInterviewerAndSuUsersWithExtractedOrValidatedStates() {
        AuthenticatedUserTestHelper provider = new AuthenticatedUserTestHelper();
        return Stream.of(
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER), SurveyUnitFakeService.SURVEY_UNIT4_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER), SurveyUnitFakeService.SURVEY_UNIT5_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT), SurveyUnitFakeService.SURVEY_UNIT4_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT), SurveyUnitFakeService.SURVEY_UNIT5_ID));
    }
}
