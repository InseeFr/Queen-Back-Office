package fr.insee.queen.api.configuration.springdoc;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import org.springframework.security.access.prepost.PreAuthorize;

public class DummyController {

    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public void testMethodHasAnyRole() {}

    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.HAS_ROLE_INTERVIEWER)
    public void testMethodAdminOrInterviewer() {}

    public void testMethodNoPreauthorize() {}
}
