package fr.insee.queen.application.web.authentication;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
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

    /**
     *
     * @param roles roles to check
     * @return true if current user has a specific role, false otherwise
     */
    boolean hasRole(AuthorityRoleEnum... roles);
}