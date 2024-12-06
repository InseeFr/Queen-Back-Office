package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    private final AuthenticationHelper authenticationHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        Authentication authentication = authenticationHelper.getAuthenticationPrincipal();

        String userId = authentication.getName().toUpperCase();

        MDC.put("id", fishTag);
        MDC.put("path", operationPath);
        MDC.put("method", method);
        MDC.put("user", userId);

        log.info("[{}] {} {}", userId, method, operationPath);
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