package fr.insee.queen.application.configuration.rest;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Add interceptor to restTemplate to inject tokens when oidc is enabled
 */
@RequiredArgsConstructor
public class RestTemplateTokenInterceptor implements ClientHttpRequestInterceptor {

    private final AuthenticationHelper authenticationHelper;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String jwt = authenticationHelper.getUserToken();
        headers.setBearerAuth(jwt);
        return execution.execute(request, body);
    }
}