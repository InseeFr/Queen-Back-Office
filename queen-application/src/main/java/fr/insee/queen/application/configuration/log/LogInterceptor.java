package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    private final AuthenticationHelper authenticationHelper;
    private static final String ANONYMOUS_ID = "ANONYMOUSUSER";

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        injectLogContext(request);
        return true;
    }


    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView mv) {
        // no need to posthandle things for this interceptor
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler,
                                Exception exception) {
        clearLogContext();
    }

    public void injectLogContext(HttpServletRequest request) {
        Authentication auth = authenticationHelper.getAuthenticationPrincipal();
        String userId = auth != null ? auth.getName() : null;
        injectLogContext(request, userId);
    }

    public void injectLogContext(HttpServletRequest request, String userId) {
        if(userId == null) {
            userId = ANONYMOUS_ID;
        }

        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        MDC.put("id", fishTag);
        MDC.put("path", operationPath);
        MDC.put("method", method);
        MDC.put("user", userId.toUpperCase());

        log.info("[{}] {} {}", userId, method, operationPath);
    }

    public void clearLogContext() {
        MDC.clear();
    }
}