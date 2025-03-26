package fr.insee.queen.application.interrogation.controller;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.application.interrogation.controller.exception.LockedResourceException;
import fr.insee.queen.application.interrogation.dto.input.StateDataInput;
import fr.insee.queen.application.interrogation.dto.input.StateDataTypeInput;
import fr.insee.queen.application.interrogation.service.dummy.StateDataFakeService;
import fr.insee.queen.application.interrogation.service.dummy.InterrogationFakeService;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
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
    private InterrogationFakeService interrogationFakeService;
    private StateDataFakeService stateDataFakeService;
    private AuthenticationFakeHelper authenticationFakeHelper;
    private AuthenticatedUserTestHelper authenticationUserProvider;

    @BeforeEach
    void init() {
        authenticationUserProvider = new AuthenticatedUserTestHelper();
        pilotageFakeComponent = new PilotageFakeComponent();
        interrogationFakeService = new InterrogationFakeService();
        stateDataFakeService = new StateDataFakeService();
        authenticationFakeHelper = new AuthenticationFakeHelper();
        stateDataController = new StateDataController(stateDataFakeService, interrogationFakeService, pilotageFakeComponent, authenticationFakeHelper);
    }


    @Test
    @DisplayName("Should throw exception when role is reviewer and campaign is sensitive")
    void testUpdateStateDataException() {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.REVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when & then
        assertThatThrownBy(() -> stateDataController.setStateData(InterrogationFakeService.INTERROGATION3_ID, stateDataInput))
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
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        stateDataController.setStateData(InterrogationFakeService.INTERROGATION3_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @ParameterizedTest
    @MethodSource("provideInterviewerAndSuUsers")
    @DisplayName("Should update data when campaign is sensitive and role is interviewer/interrogation")
    void testUpdateStateData05(Authentication auth) throws StateDataInvalidDateException, LockedResourceException {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(auth);
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        // when
        stateDataController.setStateData(InterrogationFakeService.INTERROGATION3_ID, stateDataInput);

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
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(InterrogationFakeService.INTERROGATION3_ID);
        assertThat(stateData.state()).isNotIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when
        stateDataController.setStateData(InterrogationFakeService.INTERROGATION3_ID, stateDataInput);

        // then
        assertThat(pilotageFakeComponent.isChecked()).isTrue();
        assertThat(StateDataInput.toModel(stateDataInput)).isEqualTo(stateDataFakeService.getStateDataSaved());
    }

    @ParameterizedTest
    @CsvSource(value = {InterrogationFakeService.INTERROGATION4_ID, InterrogationFakeService.INTERROGATION5_ID})
    @DisplayName("Should update data when campaign is sensitive, role is interviewer and state is EXTRACTED/VALIDATED")
    void testUpdateStateDataException02(String interrogationId) {
        // given
        StateDataInput stateDataInput = new StateDataInput(StateDataTypeInput.INIT, "1.0");
        authenticationFakeHelper.setAuthenticationUser(authenticationUserProvider.getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER));
        InterrogationSummary interrogationSummary = interrogationFakeService.getSummaryById(interrogationId);
        assertThat(interrogationSummary.campaign().getSensitivity()).isEqualTo(CampaignSensitivity.SENSITIVE);

        StateData stateData = stateDataFakeService.getStateData(interrogationId);
        assertThat(stateData.state()).isIn(StateDataType.EXTRACTED, StateDataType.VALIDATED);

        // when & then
        assertThatThrownBy(() -> stateDataController.setStateData(interrogationId, stateDataInput))
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
