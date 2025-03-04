package fr.insee.queen.application.web.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    /**
     * Retrieve the auth token of the current user
     *
     * @return auth token
     */
    String getUserToken();

    /**
     * Retrieve the authentication principal for current user
     *
     * @return {@link Authentication} the authentication user object
     */
    Authentication getAuthenticationPrincipal();
}