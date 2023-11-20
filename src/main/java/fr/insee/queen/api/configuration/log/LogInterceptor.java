package fr.insee.queen.api.configuration.log;

import fr.insee.queen.api.configuration.auth.AuthConstants;
import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import java.util.UUID;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private final ApplicationProperties applicationProperties;

    public LogInterceptor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = AuthConstants.GUEST;
        if (applicationProperties.auth().equals(AuthEnumProperties.OIDC)
                && authentication.getCredentials() instanceof Jwt jwt) {
            userId = jwt.getClaims().get("preferred_username").toString();
        }

        MDC.put("id", fishTag);
        MDC.put("path", operationPath);
        MDC.put("method", method);
        MDC.put("user", userId);

        log.info("[{}] - [{}] - [{}]", userId, method, operationPath);
        return true;
    }


    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView mv) {
        // no need to posthandle things for this interceptor
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler,
                                Exception exception) {
        MDC.clear();
    }
}