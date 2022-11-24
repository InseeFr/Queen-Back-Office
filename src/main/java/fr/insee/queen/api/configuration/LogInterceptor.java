package fr.insee.queen.api.configuration;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
   // @Value("${server.contextPath}")
    // private String contextPath;

    @Autowired
    ApplicationProperties applicationProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        String userId = null;

        switch (applicationProperties.getMode()) {
            case basic:
                Object basic = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (basic instanceof UserDetails) {
                    userId = ((UserDetails)basic).getUsername();
                } else {
                    userId = basic.toString();
                }
                break;
            case keycloak:
                KeycloakAuthenticationToken keycloak = (KeycloakAuthenticationToken) request.getUserPrincipal();
                if(keycloak!=null) {
                    userId = keycloak.getPrincipal().toString();
                }
                break;
            default:
                userId = "GUEST";
                break;
        }


        ThreadContext.put("id", fishTag);
        ThreadContext.put("path", operationPath);
        ThreadContext.put("method", method);
        ThreadContext.put("user", userId);

        logger.info("["+userId+"] - ["+method+"] - ["+operationPath+"]");
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                    Exception exception) throws Exception {
        ThreadContext.clearMap();
    }
}
