package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.log.InvalidTokenUserExtractor;
import fr.insee.queen.application.configuration.log.LogInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final InvalidTokenUserExtractor tokenUserExtractor;
    private final LogInterceptor logInterceptor;
    private final JsonMapper mapper;
    public static final String ANONYMOUS_ID = "ANONYMOUSUSER";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException
    {

        Authentication auth = authException.getAuthenticationRequest();
        String userId = null;

        if(auth != null) {
            userId = auth.getName();
        }

        if(auth instanceof BearerTokenAuthenticationToken invalidAuthToken) {
            String invalidToken = invalidAuthToken.getName();
            userId = tokenUserExtractor.extractUserId(invalidToken);
        }

        if(userId == null) {
            userId = ANONYMOUS_ID;
        }

        logInterceptor.injectLogContext(request, userId);
        log.warn(authException.getMessage());
        logInterceptor.clearLogContext();

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(status.getReasonPhrase());
        problem.setDetail("Unauthorized access");
        problem.setInstance(URI.create(request.getRequestURI()));
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), problem);
    }
}