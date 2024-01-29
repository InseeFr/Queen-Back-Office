package fr.insee.queen.application.configuration.auth;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('INTERVIEWER', 'REVIEWER', 'WEBCLIENT', 'ADMIN', 'REVIEWER_ALTERNATIVE', 'SURVEY_UNIT')";
    public static final String HAS_INTERVIEWER_PRIVILEGES = "hasAnyRole('INTERVIEWER','ADMIN','WEBCLIENT')";
    public static final String HAS_MANAGEMENT_PRIVILEGES = "hasAnyRole('INTERVIEWER', 'REVIEWER', 'WEBCLIENT', 'ADMIN', 'REVIEWER_ALTERNATIVE')";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('ADMIN', 'WEBCLIENT')";


}