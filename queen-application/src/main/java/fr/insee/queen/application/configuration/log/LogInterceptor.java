package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final Pattern TRACE_PATTERN = Pattern.compile(
            "^\\d{2}-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}$"
    );
    private final AuthenticationHelper authenticationHelper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {

        String traceIdentifier = extractTraceIdentifier(request);
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        Authentication authentication = authenticationHelper.getAuthenticationPrincipal();

        String userId = authentication.getName();

        MDC.put("id", traceIdentifier);
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

    /**
     * @param request current http servlet request
     * @return a trace identifier for the current request
     */
    private String extractTraceIdentifier(HttpServletRequest request) {
        String traceParent = request.getHeader("traceparent");

        if (!isTraceValid(traceParent)) {
            String version = "00";
            String traceId = UUID.randomUUID().toString().replace("-", "");
            String spanId = traceId.substring(16);
            String traceFlags = "01";
            traceParent = String.format("%s-%s-%s-%s", version, traceId, spanId, traceFlags);
        }
        return traceParent;
    }

    /**
     * Check trace identifier validity
     * @param traceIdentifier identifier to check
     * @return true if valid, false otherwise
     */
    private boolean isTraceValid(String traceIdentifier) {
        if (traceIdentifier == null) {
            return false;
        }
        Matcher matcher = TRACE_PATTERN.matcher(traceIdentifier);
        return matcher.matches();
    }
}