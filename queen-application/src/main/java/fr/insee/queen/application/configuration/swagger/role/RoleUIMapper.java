package fr.insee.queen.application.configuration.swagger.role;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;

import java.util.List;

/**
 * Enum linking privileges to associated roles
 */
public enum RoleUIMapper {
    ADMIN(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT),
    MANAGEMENT(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER),
    USER(AuthorityPrivileges.HAS_USER_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER,
            AuthorityRoleEnum.SURVEY_UNIT),

    INTERVIEWER(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.INTERVIEWER);

    private final String roleExpression;

    private final List<AuthorityRoleEnum> roles;

    RoleUIMapper(String roleExpression, AuthorityRoleEnum... roles) {
        this.roleExpression = roleExpression;
        this.roles = List.of(roles);
    }

    public String getRoleExpression() {
        return roleExpression;
    }

    public List<AuthorityRoleEnum> getRoles() {
        return roles;
    }
}
