package fr.insee.queen.application.configuration.swagger.role;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class DisplayRolesOnSwaggerUI implements OperationCustomizer {
    public static final String AUTHORIZED_ROLES = "Authorized roles: ";

    /**
     * Display roles allowed to use an endpoint in the description field
     * @param operation spring doc operation endpoint
     * @param handlerMethod method handler used to retrieve annotations
     * @return the operation with role description for UI
     */
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        var preAuthorizeAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        StringBuilder description = new StringBuilder();
        if(preAuthorizeAnnotation == null) {
            return operation;
        }
        if(operation.getDescription() != null) {
            description
                    .append(operation.getDescription())
                    .append("\n");
        }
        description.append(AUTHORIZED_ROLES);
        String roles = preAuthorizeAnnotation.value();
        List<AuthorityRoleEnum> rolesAuthority = Arrays.stream(RoleUIMapper.values())
                .filter(roleUIMapper -> roles.contains(roleUIMapper.getRoleExpression()))
                .map(RoleUIMapper::getRoles)
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        for(AuthorityRoleEnum roleAuthority : rolesAuthority) {
            description
                    .append(roleAuthority.name())
                    .append(" / ");
        }
        operation.setDescription(description.toString());
        return operation;
    }
}
