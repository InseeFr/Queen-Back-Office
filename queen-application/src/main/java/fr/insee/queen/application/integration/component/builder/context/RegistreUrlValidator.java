package fr.insee.queen.application.integration.component.builder.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Utilitaire pour valider les URLs.
 */
@Component
public class RegistreUrlValidator {

    private final String baseUrl;

    public RegistreUrlValidator(@Value("${feature.registre.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) return false;
        if (baseUrl == null || baseUrl.isBlank() || !url.startsWith(baseUrl)) return false;
        try {
            URI.create(url).toURL();
            return true;
        } catch (IllegalArgumentException | MalformedURLException e) {
            return false;
        }
    }
}