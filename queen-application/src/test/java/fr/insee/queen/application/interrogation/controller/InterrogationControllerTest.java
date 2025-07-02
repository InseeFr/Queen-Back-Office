package fr.insee.queen.application.interrogation.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.interrogation.dto.output.InterrogationBySurveyUnitDto;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.controller.dummy.MetadataFakeConverter;
import fr.insee.queen.application.interrogation.controller.exception.LockedResourceException;
import fr.insee.queen.application.interrogation.dto.input.*;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
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

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InterrogationControllerTest {
    private InterrogationController interrogationController;
    private PilotageFakeComponent pilotageComponent;
    private InterrogationFakeService interrogationFakeService;
    private MetadataFakeConverter metadataConverter;
    private StateDataFakeService stateDataService;
    private AuthenticationFakeHelper authenticatedUserHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        metadataConverter = new MetadataFakeConverter();
        pilotageComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        stateDataService = new StateDataFakeService();
        authenticatedUserHelper = new AuthenticationFakeHelper();
        interrogationController = new InterrogationController(interrogationFakeService, pilotageComponent, metadataConverter, stateDataService, authenticatedUserHelper);
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testDiffUpdateInterrogationException() {
        // given
        InterrogationDataStateDataUpdateInput suInput = new InterrogationDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> interrogationController.updateInterrogationDataStateDataById(InterrogationFakeService.INTERROGATION3_ID, suInput))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {InterrogationFakeService.INTERROGATION4_ID, InterrogationFakeService.INTERROGATION5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testDiffUpdateInterrogationException02(String interrogationId) {

        // given
        InterrogationDataStateDataUpdateInput suInput = new InterrogationDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(interrogationId);
        assertThat(interrogation.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> interrogationController.updateInterrogationDataStateDataById(interrogationId, suInput))
                .isInstanceOf(LockedResourceException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update interrogation when campaign is sensitive and role is admin/webclient")
    void testDiffUpdateInterrogation04() throws LockedResourceException {
        // given
        InterrogationDataStateDataUpdateInput suInput = new InterrogationDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        interrogationController.updateInterrogationDataStateDataById(InterrogationFakeService.INTERROGATION3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.id()).isEqualTo(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(interrogationUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(interrogationUpdated.data()).isEqualTo(suInput.data());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update interrogation when campaign is sensitive and role is interviewer/interrogation")
    void testDiffUpdateInterrogation05(Authentication auth) throws LockedResourceException {
        // given
        InterrogationDataStateDataUpdateInput suInput = new InterrogationDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        interrogationController.updateInterrogationDataStateDataById(InterrogationFakeService.INTERROGATION3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.id()).isEqualTo(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(interrogationUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(interrogationUpdated.data()).isEqualTo(suInput.data());
    }

    @Test
    @DisplayName("Should update interrogation when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testDiffUpdateInterrogation06() throws LockedResourceException {
        // given
        InterrogationDataStateDataUpdateInput suInput = new InterrogationDataStateDataUpdateInput(null, new StateDataInput(StateDataTypeInput.INIT, "2.0"));
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogation.stateData().state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        interrogationController.updateInterrogationDataStateDataById(InterrogationFakeService.INTERROGATION3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.id()).isEqualTo(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationUpdated.stateData().state()).isEqualTo(StateDataType.valueOf(suInput.stateData().state().name()));
        assertThat(interrogationUpdated.stateData().currentPage()).isEqualTo(suInput.stateData().currentPage());
        assertThat(interrogationUpdated.data()).isEqualTo(suInput.data());
    }

    @Test
    @DisplayName("Should update interrogation and transform state-data to null if state from input state data is null")
    void testUpdateInterrogation01() throws LockedResourceException {
        // given
        StateDataForInterrogationUpdateInput stateData = new StateDataForInterrogationUpdateInput(null, 123456789L, "2.3");
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, stateData);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should update interrogation and transform state-data to null if input state data is null")
    void testUpdateInterrogation02() throws LockedResourceException {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, null);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION1_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateInterrogationException() {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION3_ID, suInput))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should update interrogation when campaign is sensitive and role is admin/webclient")
    void testUpdateInterrogation04() throws LockedResourceException {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @Test
    @DisplayName("Should update interrogation when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testUpdateInterrogation06() throws LockedResourceException {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogation.stateData().state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        interrogationController.updateInterrogationById(InterrogationFakeService.INTERROGATION3_ID, suInput);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated.stateData()).isNull();
    }

    @ParameterizedTest
    @CsvSource(value = {InterrogationFakeService.INTERROGATION4_ID, InterrogationFakeService.INTERROGATION5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateInterrogationException02(String interrogationId) {
        // given
        InterrogationUpdateInput suInput = new InterrogationUpdateInput(null, null, null, null);
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(interrogationId);
        assertThat(interrogation.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> interrogationController.updateInterrogationById(interrogationId, suInput))
                .isInstanceOf(LockedResourceException.class);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        Interrogation interrogationUpdated = interrogationFakeService.getInterrogationUpdated();
        assertThat(interrogationUpdated).isNull();
    }

    @Test
    @DisplayName("Should return interrogation with data when campaign is not sensitive")
    void testGetInterrogation01() {
        // given
        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION1_ID);

        // then
        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION1_ID);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isEqualTo(interrogation.data());
        assertThat(interrogationDto.comment()).isEqualTo(interrogation.comment());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData().state()).isEqualTo(interrogation.stateData().state());
        assertThat(interrogationDto.stateData().currentPage()).isEqualTo(interrogation.stateData().currentPage());
        assertThat(interrogationDto.stateData().date()).isEqualTo(interrogation.stateData().date());
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testGetInterrogationException() {
        // given
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION3_ID))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndWebclientUsers")
    @DisplayName("Should return interrogation with data when campaign is sensitive and role is admin/webclient")
    void testGetInterrogation02() {
        // given
        authenticatedUserHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());

        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION3_ID);

        // then
        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION3_ID);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isEqualTo(interrogation.data());
        assertThat(interrogationDto.comment()).isEqualTo(interrogation.comment());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData().state()).isEqualTo(interrogation.stateData().state());
        assertThat(interrogationDto.stateData().currentPage()).isEqualTo(interrogation.stateData().currentPage());
        assertThat(interrogationDto.stateData().date()).isEqualTo(interrogation.stateData().date());
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return interrogation with data when campaign is sensitive and role is interviewer/interrogation")
    void testGetInterrogation03(Authentication auth) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION3_ID);

        // then
        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION3_ID);

        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isEqualTo(interrogation.data());
        assertThat(interrogationDto.comment()).isEqualTo(interrogation.comment());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData().state()).isEqualTo(interrogation.stateData().state());
        assertThat(interrogationDto.stateData().currentPage()).isEqualTo(interrogation.stateData().currentPage());
        assertThat(interrogationDto.stateData().date()).isEqualTo(interrogation.stateData().date());
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsersWithExtractedOrValidatedStates")
    @DisplayName("Should return interrogation without data when campaign is sensitive, role is interviewer/survey-unit and state is EXTRACTED/VALIDATED")
    void testGetInterrogation04(Authentication auth, String interrogationId) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(interrogationId);
        assertThat(interrogation.stateData().state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(interrogationId);

        // then

        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isNotEqualTo(interrogation.data());
        assertThat(interrogationDto.data()).isEqualTo(JsonNodeFactory.instance.objectNode());
        assertThat(interrogationDto.comment()).isEqualTo(interrogation.comment());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData().state()).isEqualTo(interrogation.stateData().state());
        assertThat(interrogationDto.stateData().currentPage()).isEqualTo(interrogation.stateData().currentPage());
        assertThat(interrogationDto.stateData().date()).isEqualTo(interrogation.stateData().date());
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return interrogation with data when campaign is sensitive, role is interviewer/survey-unit, and state data is null")
    void testGetInterrogation05(Authentication auth) {
        // given
        authenticatedUserHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION6_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        Interrogation interrogation = interrogationFakeService.getInterrogation(InterrogationFakeService.INTERROGATION6_ID);
        assertThat(interrogation.stateData()).isNull();

        // when
        InterrogationDto interrogationDto = interrogationController.getInterrogationById(InterrogationFakeService.INTERROGATION6_ID);

        // then
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(interrogationDto.id()).isEqualTo(interrogation.id());
        assertThat(interrogationDto.data()).isEqualTo(interrogation.data());
        assertThat(interrogationDto.comment()).isEqualTo(interrogation.comment());
        assertThat(interrogationDto.personalization()).isEqualTo(interrogation.personalization());
        assertThat(interrogationDto.stateData()).isNull();
        assertThat(interrogationDto.questionnaireId()).isEqualTo(interrogation.questionnaireId());
    }

    @Test
    @DisplayName("Should return interrogations by survey-unit")
    void testGetInterrogationsBySurveyUnitId() {
        // when
        List<InterrogationBySurveyUnitDto> interrogations = interrogationController.getInterrogationsBySurveyUnit("survey-unit-id1");

        // then
        assertThat(interrogations).isNotNull()
                .hasSize(1);
        InterrogationBySurveyUnitDto interrogation = interrogations.getFirst();
        assertThat(interrogation).isNotNull();
        assertThat(interrogation.interrogationId()).isEqualTo(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogation.campaignId()).isEqualTo("campaign-id");
    }

    @Test
    @DisplayName("Should return empty list  if survey-unit unknown")
    void testGetInterrogationsBySurveyUnitId2() {
        // when
        List<InterrogationBySurveyUnitDto> interrogations = interrogationController.getInterrogationsBySurveyUnit("survey-unit-unknown");

        // then
        assertThat(interrogations).isNotNull()
                .isEmpty();
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
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER), InterrogationFakeService.INTERROGATION4_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER), InterrogationFakeService.INTERROGATION5_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT), InterrogationFakeService.INTERROGATION4_ID),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT), InterrogationFakeService.INTERROGATION5_ID));
    }
}
