package fr.insee.queen.application.configuration.auth;

public class AuthorityRole {
    private AuthorityRole() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_ROLE_INTERVIEWER = "hasRole('INTERVIEWER')";
    public static final String HAS_ANY_ROLE = "hasAnyRole('INTERVIEWER', 'REVIEWER', 'WEBCLIENT', 'ADMIN', 'REVIEWER_ALTERNATIVE')";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('ADMIN', 'WEBCLIENT')";
}