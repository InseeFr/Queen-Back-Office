package fr.insee.queen.application.configuration.springdoc;

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
        Method method = controller.getClass().getMethod("testMethodHasAnyRole");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation.getDescription())
                .isEqualTo(DisplayRolesOnSwaggerUI.AUTHORIZED_ROLES + RoleUIMapper.AUTHENTICATED + " / ");

    }

    @Test
    @DisplayName("on generate operation, when description is set return it before roles")
    void testOperation03() throws NoSuchMethodException {
        String description = "description";
        operation.setDescription(description);
        Method method = controller.getClass().getMethod("testMethodHasAnyRole");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation.getDescription())
                .startsWith(description +"\n");
    }

    @Test
    @DisplayName("on generate operation, when preauthorize annotation with multiples roles is set return roles in operation description")
    void testOperation04() throws NoSuchMethodException {
        Method method = controller.getClass().getMethod("testMethodAdminOrInterviewer");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);
        Operation resultOperation = operationCustomizer.customize(operation, handlerMethod);

        assertThat(resultOperation.getDescription())
                .isEqualTo(DisplayRolesOnSwaggerUI.AUTHORIZED_ROLES + RoleUIMapper.ADMIN + " / " + RoleUIMapper.INTERVIEWER + " / ");

    }
}
