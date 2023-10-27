package fr.insee.queen.api.controller.utils;

import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    String getAuthToken(Authentication auth);
    String getUserId(Authentication authentication);
}