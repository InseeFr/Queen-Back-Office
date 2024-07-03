package fr.insee.queen.application.configuration.swagger.role;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import lombok.Getter;

import java.util.List;

/**
 * Enum linking privileges to associated roles
 */
@Getter
public enum RoleUIMapper {
    USER(AuthorityPrivileges.HAS_USER_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER,
            AuthorityRoleEnum.SURVEY_UNIT),

    SURVEY_UNIT(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.INTERVIEWER,
            AuthorityRoleEnum.SURVEY_UNIT),

    ADMIN(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT),

    REVIEWER(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT,
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER),

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
}
