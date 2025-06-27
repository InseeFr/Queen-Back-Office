package fr.insee.queen.application.interrogation.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.controller.exception.LockedResourceException;
import fr.insee.queen.application.interrogation.service.dummy.DataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
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
    private InterrogationFakeService interrogationFakeService;
    private StateDataFakeService stateDataFakeService;
    private DataFakeService dataFakeService;
    private AuthenticationFakeHelper authenticationFakeHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        pilotageFakeComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        stateDataFakeService = new StateDataFakeService();
        dataFakeService = new DataFakeService();
        authenticationFakeHelper = new AuthenticationFakeHelper();
        dataController = new DataController(dataFakeService, pilotageFakeComponent, stateDataFakeService, interrogationFakeService, authenticationFakeHelper);
    }
    
    
    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateInterrogationException() {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> dataController.updateData(dataInput, InterrogationFakeService.INTERROGATION3_ID))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideReviewerUsers")
    @DisplayName("Should update data when campaign is sensitive and role is admin/webclient")
    void testUpdateInterrogation04() throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        dataController.updateData(dataInput, InterrogationFakeService.INTERROGATION3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update data when campaign is sensitive and role is interviewer/interrogation")
    void testUpdateInterrogation05(Authentication auth) throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        dataController.updateData(dataInput, InterrogationFakeService.INTERROGATION3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @Test
    @DisplayName("Should update data when campaign is sensitive, role is interviewer and state is not EXTRACTED/VALIDATED")
    void testUpdateInterrogation06() throws LockedResourceException {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(stateData.state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        dataController.updateData(dataInput, InterrogationFakeService.INTERROGATION3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {InterrogationFakeService.INTERROGATION4_ID, InterrogationFakeService.INTERROGATION5_ID})
    @DisplayName("Should throw exception when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateInterrogationException02(String interrogationId) {
        // given
        ObjectNode dataInput = JsonNodeFactory.instance.objectNode();
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(interrogationId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when

        assertThatThrownBy(() -> dataController.updateData(dataInput, interrogationId))
                .isInstanceOf(LockedResourceException.class);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(dataFakeService.isCheckUpdateData()).isFalse();
    }

    @Test
    @DisplayName("Should return data when campaign is not sensitive")
    void testGetInterrogation01() {
        // given
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.NORMAL);
        
        // when
        ObjectNode data = dataController.getDataByInterrogation(InterrogationFakeService.INTERROGATION1_ID);

        // then
        ObjectNode interrogationData = dataFakeService.getData(InterrogationFakeService.INTERROGATION1_ID);
        assertThat(data).isEqualTo(interrogationData);
    }
    @ParameterizedTest
    @MethodSource("provideReviewerUsers")
    @DisplayName("Should return data when campaign is sensitive and role is admin/webclient/reviewer")
    void testGetInterrogation02() {
        // given
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAdminUser());
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        ObjectNode data = dataController.getDataByInterrogation(InterrogationFakeService.INTERROGATION3_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        ObjectNode interrogationData = dataFakeService.getData(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(data).isEqualTo(interrogationData);
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return data when campaign is sensitive and role is interviewer/interrogation")
    void testGetInterrogation03(Authentication auth) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        ObjectNode data = dataController.getDataByInterrogation(InterrogationFakeService.INTERROGATION3_ID);

        // then
        ObjectNode interrogationData = dataFakeService.getData(InterrogationFakeService.INTERROGATION3_ID);

        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data).isEqualTo(interrogationData);
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsersWithExtractedOrValidatedStates")
    @DisplayName("Should return empty data when campaign is sensitive, role is interviewer/interrogation and state is EXTRACTED/VALIDATED")
    void testGetInterrogation04(Authentication auth, String interrogationId) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        ObjectNode interrogationData = dataFakeService.getData(interrogationId);
        StateData stateData = stateDataFakeService.getStateData(interrogationId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        ObjectNode data = dataController.getDataByInterrogation(interrogationId);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data)
                .isNotEqualTo(interrogationData)
                .isEqualTo(JsonNodeFactory.instance.objectNode());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should return data when campaign is sensitive, role is interviewer/interrogation, and state data is null")
    void testGetInterrogation05(Authentication auth) {
        // given
        authenticationFakeHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION6_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        ObjectNode interrogationData = dataFakeService.getData(InterrogationFakeService.INTERROGATION6_ID);
        StateData stateData = stateDataFakeService.getStateData(InterrogationFakeService.INTERROGATION6_ID);
        assertThat(stateData).isNull();

        // when
        ObjectNode data = dataController.getDataByInterrogation(InterrogationFakeService.INTERROGATION6_ID);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(data).isEqualTo(interrogationData);
    }

    private static Stream<Arguments> provideInterviewerAndSuUsers() {
        AuthenticatedUserTestHelper provider = new AuthenticatedUserTestHelper();
        return Stream.of(
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER)),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT)));
    }

    private static Stream<Arguments> provideReviewerUsers() {
        AuthenticatedUserTestHelper provider = new AuthenticatedUserTestHelper();
        return Stream.of(
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.WEBCLIENT)),
                Arguments.of(provider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER)),
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
