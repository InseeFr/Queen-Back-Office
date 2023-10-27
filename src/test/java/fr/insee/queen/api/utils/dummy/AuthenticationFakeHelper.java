package fr.insee.queen.api.utils.dummy;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import org.springframework.security.core.Authentication;

public class AuthenticationFakeHelper implements AuthenticationHelper {
    @Override
    public String getAuthToken(Authentication auth) {
        return null;
    }

    @Override
    public String getUserId(Authentication authentication) {
        if(authentication == null) {
            return Constants.GUEST;
        }
        return authentication.getName();
    }
}
