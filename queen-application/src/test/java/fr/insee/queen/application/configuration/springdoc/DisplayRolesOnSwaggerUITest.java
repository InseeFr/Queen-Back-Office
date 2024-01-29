package fr.insee.queen.application.configuration.springdoc;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.configuration.swagger.role.DisplayRolesOnSwaggerUI;
import fr.insee.queen.application.configuration.swagger.role.RoleUIMapper;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class DisplayRolesOnSwaggerUITest {

    private DummyController controller;
    private Operation operation;
    private DisplayRolesOnSwaggerUI operationCustomizer;
    @BeforeEach
    void init() {
        controller = new DummyController();
        operation = new Operation();
        operationCustomizer = new DisplayRolesOnSwaggerUI();
    }

    @Test
    @DisplayName("on generate operation, when preauthorize annotation is not set do nothing")
    void testOperation01() throws NoSuchMethodException {
        Method method = controller.getClass().getMethod("testMethodNoPreauthorize");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation)
                .isEqualTo(operation);
    }

    @Test
    @DisplayName("on generate operation, when preauthorize annotation is set return roles in operation description")
    void testOperation02() throws NoSuchMethodException {
        Method method = controller.getClass().getMethod("testMethodHasUserPrivileges");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        for(AuthorityRoleEnum role : RoleUIMapper.ADMIN.getRoles()) {
            assertThat(resultOperation.getDescription())
                    .contains(role.name());
        }
    }

    @Test
    @DisplayName("on generate operation, when multiple privileges, return unique roles in operation description")
    void testOperation03() throws NoSuchMethodException {
        String description = "description";
        operation.setDescription(description);
        Method method = controller.getClass().getMethod("testMethodMultiplePrivileges");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation.getDescription())
                .startsWith(description +"\n");

        for(AuthorityRoleEnum role : RoleUIMapper.ADMIN.getRoles()) {
            assertThat(resultOperation.getDescription())
                    .containsOnlyOnce(role.name());
        }

        for(AuthorityRoleEnum role : RoleUIMapper.INTERVIEWER.getRoles()) {
            assertThat(resultOperation.getDescription())
                    .containsOnlyOnce(role.name());
        }
    }

    @Test
    @DisplayName("on generate operation, when description is set return it before roles")
    void testOperation04() throws NoSuchMethodException {
        String description = "description";
        operation.setDescription(description);
        Method method = controller.getClass().getMethod("testMethodMultiplePrivileges");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation.getDescription())
                .startsWith(description +"\n");
    }
}
