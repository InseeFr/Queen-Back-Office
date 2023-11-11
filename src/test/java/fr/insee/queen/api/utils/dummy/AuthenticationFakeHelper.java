package fr.insee.queen.api.utils.dummy;

import fr.insee.queen.api.configuration.auth.AuthConstants;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import org.springframework.security.core.Authentication;

public class AuthenticationFakeHelper implements AuthenticationHelper {
    @Override
    public String getAuthToken(Authentication auth) {
        return null;
    }

    @Override
    public String getUserId(Authentication authentication) {
        if (authentication == null) {
            return AuthConstants.GUEST;
        }
        return authentication.getName();
    }
}
