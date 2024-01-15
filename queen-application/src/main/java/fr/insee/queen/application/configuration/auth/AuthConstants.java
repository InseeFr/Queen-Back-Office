package fr.insee.queen.application.configuration.auth;

public class AuthConstants {
    private AuthConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String GUEST = "GUEST";
}
