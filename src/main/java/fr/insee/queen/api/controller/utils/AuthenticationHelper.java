package fr.insee.queen.api.controller.utils;

import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    /**
     * Retrieve the auth token of the current user
     * @param auth authentication object
     * @return auth token
     */
    String getAuthToken(Authentication auth);

    /**
     * Retrieve the user id from the current user
     * @param authentication authentication object
     * @return the user id
     */
    String getUserId(Authentication authentication);
}