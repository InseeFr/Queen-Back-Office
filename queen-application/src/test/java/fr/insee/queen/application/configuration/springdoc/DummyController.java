package fr.insee.queen.application.configuration.springdoc;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import org.springframework.security.access.prepost.PreAuthorize;

public class DummyController {

    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void testMethodHasUserPrivileges() {}

    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES + "||" + AuthorityPrivileges.HAS_ADMIN_PRIVILEGES )
    public void testMethodMultiplePrivileges() {}

    public void testMethodNoPreauthorize() {}
}
