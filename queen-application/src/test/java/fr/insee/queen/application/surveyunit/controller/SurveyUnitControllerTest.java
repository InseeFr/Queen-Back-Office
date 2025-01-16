package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.surveyunit.controller.dummy.MetadataFakeConverter;
import fr.insee.queen.application.surveyunit.controller.exception.ConflictException;
import fr.insee.queen.application.surveyunit.dto.input.*;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.application.surveyunit.service.dummy.StateDataFakeService;
import fr.insee.queen.application.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.Authentication;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.security.access.AccessDeniedException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitControllerTest {
    private SurveyUnitController surveyUnitController;
    private PilotageFakeComponent pilotageComponent;
    private SurveyUnitFakeService surveyUnitFakeService;
    private MetadataFakeConverter metadataConverter;
    private StateDataFakeService stateDataService;
    private AuthenticationFakeHelper authenticatedUserHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    public void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        metadataConverter = new MetadataFakeConverter();
        pilotageComponent = new PilotageFakeComponent();
        surveyUnitFakeService = new SurveyUnitFakeService();
        stateDataService = new StateDataFakeService();
        authenticatedUserHelper = new AuthenticationFakeHelper();
        surveyUnitController = new SurveyUnitController(surveyUnitFakeService, pilotageComponent, metadataConverter, stateDataService, authenticatedUserHelper);
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testDiffUpdateSurveyUnitException() {
        // given
        SurveyUnitDataStateDataUpdateInput suInput = new SurveyUnitDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, 1L, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> surveyUnitController.updateSurveyUnitDataStateDataById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {SurveyUnitFakeService.SURVEY_UNIT4_ID, SurveyUnitFakeService.SURVEY_UNIT5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testDiffUpdateSurveyUnitException02(String surveyUnitId) {

        // given
        SurveyUnitDataStateDataUpdateInput suInput = new SurveyUnitDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, 1L, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(surveyUnitId);
        assertThat(surveyUnit.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> surveyUnitController.updateSurveyUnitDataStateDataById(surveyUnitId, suInput))
                .isInstanceOf(ConflictException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update survey unit when campaign is sensitive and role is admin/webclient")
    void testDiffUpdateSurveyUnit04() throws ConflictException {
        // given
        SurveyUnitDataStateDataUpdateInput suInput = new SurveyUnitDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, 1L, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        surveyUnitController.updateSurveyUnitDataStateDataById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(surveyUnitUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(surveyUnitUpdated.stateData().date()).isEqualTo(suInput.stateData().date());
        assertThat(surveyUnitUpdated.data()).isEqualTo(suInput.data());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update survey unit when campaign is sensitive and role is interviewer/survey-unit")
    void testDiffUpdateSurveyUnit05(Authentication auth) throws ConflictException {
        // given
        SurveyUnitDataStateDataUpdateInput suInput = new SurveyUnitDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, 1L, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        surveyUnitController.updateSurveyUnitDataStateDataById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(surveyUnitUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(surveyUnitUpdated.stateData().date()).isEqualTo(suInput.stateData().date());
        assertThat(surveyUnitUpdated.data()).isEqualTo(suInput.data());
    }

    @Test
    @DisplayName("Should update survey unit when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testDiffUpdateSurveyUnit06() throws ConflictException {
        // given
        SurveyUnitDataStateDataUpdateInput suInput = new SurveyUnitDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, 1L, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnit.stateData().state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        surveyUnitController.updateSurveyUnitDataStateDataById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.id()).isEqualTo(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(surveyUnitUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(surveyUnitUpdated.stateData().date()).isEqualTo(suInput.stateData().date());
        assertThat(surveyUnitUpdated.data()).isEqualTo(suInput.data());
    }

    @Test
    @DisplayName("Should update survey unit and transform state-data to null if state from input state data is null")
    void testUpdateSurveyUnit01() throws ConflictException {
        // given
        StateDataForSurveyUnitUpdateInput stateData = new StateDataForSurveyUnitUpdateInput(null, 123456789L, "2.3");
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, stateData);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);

        // when
        surveyUnitController.updateSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should update survey unit and transform state-data to null if input state data is null")
    void testUpdateSurveyUnit02() throws ConflictException {
        // given
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);

        // when
        surveyUnitController.updateSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateSurveyUnitException() {
        // given
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> surveyUnitController.updateSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update survey unit when campaign is sensitive and role is admin/webclient")
    void testUpdateSurveyUnit04() throws ConflictException {
        // given
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        surveyUnitController.updateSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should update survey unit when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testUpdateSurveyUnit06() throws ConflictException {
        // given
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnit.stateData().state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        surveyUnitController.updateSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.stateData()).isNull();
    }

    @ParameterizedTest
    @CsvSource(value = {SurveyUnitFakeService.SURVEY_UNIT4_ID, SurveyUnitFakeService.SURVEY_UNIT5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateSurveyUnitException02(String surveyUnitId) {
        // given
        SurveyUnitUpdateInput suInput = new SurveyUnitUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(surveyUnitId);
        assertThat(surveyUnit.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> surveyUnitController.updateSurveyUnitById(surveyUnitId, suInput))
                .isInstanceOf(ConflictException.class);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        SurveyUnit surveyUnitUpdated = surveyUnitFakeService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated).isNull();
    }

    @Test
    @DisplayName("Should return survey unit with data when campaign is not sensitive")
    void testGetSurveyUnit01() {
        // given
        // when
        SurveyUnitDto surveyUnitDto = surveyUnitController.getSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT1_ID);

        // then
        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT1_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(surveyUnitDto.id()).isEqualTo(surveyUnit.id());
        assertThat(surveyUnitDto.data()).isEqualTo(surveyUnit.data());
        assertThat(surveyUnitDto.comment()).isEqualTo(surveyUnit.comment());
        assertThat(surveyUnitDto.personalization()).isEqualTo(surveyUnit.personalization());
        assertThat(surveyUnitDto.stateData().state()).isEqualTo(surveyUnit.stateData().state());
        assertThat(surveyUnitDto.stateData().currentPage()).isEqualTo(surveyUnit.stateData().currentPage());
        assertThat(surveyUnitDto.stateData().date()).isEqualTo(surveyUnit.stateData().date());
        assertThat(surveyUnitDto.questionnaireId()).isEqualTo(surveyUnit.questionnaireId());
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testGetSurveyUnitException() {
        // given
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> surveyUnitController.getSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should return survey unit with data when campaign is sensitive and role is admin/webclient")
    void testGetSurveyUnit02() {
        // given
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());

        // when
        SurveyUnitDto surveyUnitDto = surveyUnitController.getSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(surveyUnitDto.id()).isEqualTo(surveyUnit.id());
        assertThat(surveyUnitDto.data()).isEqualTo(surveyUnit.data());
        assertThat(surveyUnitDto.comment()).isEqualTo(surveyUnit.comment());
        assertThat(surveyUnitDto.personalization()).isEqualTo(surveyUnit.personalization());
        assertThat(surveyUnitDto.stateData().state()).isEqualTo(surveyUnit.stateData().state());
        assertThat(surveyUnitDto.stateData().currentPage()).isEqualTo(surveyUnit.stateData().currentPage());
        assertThat(surveyUnitDto.stateData().date()).isEqualTo(surveyUnit.stateData().date());
        assertThat(surveyUnitDto.questionnaireId()).isEqualTo(surveyUnit.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return survey unit with data when campaign is sensitive and role is interviewer/survey-unit")
    void testGetSurveyUnit03(Authentication auth) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT3_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        SurveyUnitDto surveyUnitDto = surveyUnitController.getSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        // then
        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT3_ID);

        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(surveyUnitDto.id()).isEqualTo(surveyUnit.id());
        assertThat(surveyUnitDto.data()).isEqualTo(surveyUnit.data());
        assertThat(surveyUnitDto.comment()).isEqualTo(surveyUnit.comment());
        assertThat(surveyUnitDto.personalization()).isEqualTo(surveyUnit.personalization());
        assertThat(surveyUnitDto.stateData().state()).isEqualTo(surveyUnit.stateData().state());
        assertThat(surveyUnitDto.stateData().currentPage()).isEqualTo(surveyUnit.stateData().currentPage());
        assertThat(surveyUnitDto.stateData().date()).isEqualTo(surveyUnit.stateData().date());
        assertThat(surveyUnitDto.questionnaireId()).isEqualTo(surveyUnit.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsersWithExtractedOrValidatedStates")
    @DisplayName("Should return survey unit without data when campaign is sensitive, role is interviewer/survey-unit and state is EXTRACTED/VALIDATED")
    void testGetSurveyUnit04(Authentication auth, String surveyUnitId) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(surveyUnitId);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(surveyUnitId);
        assertThat(surveyUnit.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        SurveyUnitDto surveyUnitDto = surveyUnitController.getSurveyUnitById(surveyUnitId);

        // then

        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(surveyUnitDto.id()).isEqualTo(surveyUnit.id());
        assertThat(surveyUnitDto.data()).isNotEqualTo(surveyUnit.data());
        assertThat(surveyUnitDto.data()).isEqualTo(JsonNodeFactory.instance.objectNode());
        assertThat(surveyUnitDto.comment()).isEqualTo(surveyUnit.comment());
        assertThat(surveyUnitDto.personalization()).isEqualTo(surveyUnit.personalization());
        assertThat(surveyUnitDto.stateData().state()).isEqualTo(surveyUnit.stateData().state());
        assertThat(surveyUnitDto.stateData().currentPage()).isEqualTo(surveyUnit.stateData().currentPage());
        assertThat(surveyUnitDto.stateData().date()).isEqualTo(surveyUnit.stateData().date());
        assertThat(surveyUnitDto.questionnaireId()).isEqualTo(surveyUnit.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return survey unit with data when campaign is sensitive, role is interviewer/survey-unit, and state data is null")
    void testGetSurveyUnit05(Authentication auth) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        SurveyUnitSummary surveyUnitSummary = surveyUnitFakeService.getSummaryById(SurveyUnitFakeService.SURVEY_UNIT6_ID);
        assertThat(surveyUnitSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        SurveyUnit surveyUnit = surveyUnitFakeService.getSurveyUnit(SurveyUnitFakeService.SURVEY_UNIT6_ID);
        assertThat(surveyUnit.stateData()).isNull();

        // when
        SurveyUnitDto surveyUnitDto = surveyUnitController.getSurveyUnitById(SurveyUnitFakeService.SURVEY_UNIT6_ID);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(surveyUnitDto.id()).isEqualTo(surveyUnit.id());
        assertThat(surveyUnitDto.data()).isEqualTo(surveyUnit.data());
        assertThat(surveyUnitDto.comment()).isEqualTo(surveyUnit.comment());
        assertThat(surveyUnitDto.personalization()).isEqualTo(surveyUnit.personalization());
        assertThat(surveyUnitDto.stateData()).isNull();
        assertThat(surveyUnitDto.questionnaireId()).isEqualTo(surveyUnit.questionnaireId());
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
