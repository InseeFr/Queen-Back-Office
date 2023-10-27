package fr.insee.queen.api.controller.utils;

import org.springframework.security.core.Authentication;

public interface HabilitationComponent {
    void checkHabilitations(Authentication auth, String surveyUnitId, String... roles);
}
