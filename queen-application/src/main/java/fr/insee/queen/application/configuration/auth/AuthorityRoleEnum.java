package fr.insee.queen.application.configuration.auth;

public enum AuthorityRoleEnum {
    ADMIN,
    WEBCLIENT,
    REVIEWER,
    REVIEWER_ALTERNATIVE,
    INTERVIEWER,
    SURVEY_UNIT;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() {
        return ROLE_PREFIX + this.name();
    }
}
