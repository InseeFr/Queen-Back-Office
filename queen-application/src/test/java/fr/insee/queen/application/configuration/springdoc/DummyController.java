package fr.insee.queen.application.configuration.springdoc;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import org.springframework.security.access.prepost.PreAuthorize;

public class DummyController {

    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void testMethodHasUserPrivileges() {
        // used to verify ACL
    }

    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES + "||" + AuthorityPrivileges.HAS_ADMIN_PRIVILEGES )
    public void testMethodMultiplePrivileges() {
        // used to verify ACL
    }

    public void testMethodNoPreauthorize() {
        // used to verify ACL
    }
}
