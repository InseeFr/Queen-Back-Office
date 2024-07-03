package fr.insee.queen.application.configuration.auth;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('SURVEY_UNIT', 'REVIEWER', 'REVIEWER_ALTERNATIVE', 'INTERVIEWER', 'ADMIN', 'WEBCLIENT')";
    public static final String HAS_SURVEY_UNIT_PRIVILEGES = "hasAnyRole('SURVEY_UNIT', 'INTERVIEWER', 'ADMIN', 'WEBCLIENT')";
    public static final String HAS_INTERVIEWER_PRIVILEGES = "hasAnyRole('INTERVIEWER', 'ADMIN', 'WEBCLIENT')";
    public static final String HAS_REVIEWER_PRIVILEGES = "hasAnyRole('REVIEWER', 'REVIEWER_ALTERNATIVE', 'INTERVIEWER', 'ADMIN', 'WEBCLIENT')";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('ADMIN', 'WEBCLIENT')";
}