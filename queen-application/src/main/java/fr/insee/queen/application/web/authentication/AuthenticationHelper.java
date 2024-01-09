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
     * Retrieve the user id from the current user
     *
     * @return the user id
     */
    String getUserId();

    /**
     * Retrieve the authentication principal for current user
     *
     * @return {@link Authentication} the authentication user object
     */
    Authentication getAuthenticationPrincipal();
}