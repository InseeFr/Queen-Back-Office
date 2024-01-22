package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.dummy.AuthenticationFakeHelper;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class OidcSecurityConfigurationTest {
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
    private final JwtAuthenticationToken interviewerUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.INTERVIEWER);

    @BeforeEach
    public void init() {
        OidcSecurityConfiguration conf = new OidcSecurityConfiguration(null);
        AuthenticationHelper helper = new AuthenticationFakeHelper(interviewerUser);
        restTemplate = conf.restTemplatePilotage(helper);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("when using restTemplate, assure jwt authorization is integrated in requests")
    void testAuthorizationIsIntegratedInHttpRequest() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("/")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ interviewerUser.getToken().getTokenValue()))
                .andRespond(withStatus(HttpStatus.OK));

        restTemplate.getForObject("/", Object.class);
        mockServer.verify();
    }
}
