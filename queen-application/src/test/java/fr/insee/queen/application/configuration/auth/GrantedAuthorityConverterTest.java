package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.properties.OidcProperties;
import fr.insee.queen.application.configuration.properties.RoleProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GrantedAuthorityConverterTest {

    private GrantedAuthorityConverter converter;

    private OidcProperties oidcProperties;

    private static final String JWT_ROLE_INTERVIEWER = "interviewer";
    private static final String JWT_ROLE_REVIEWER = "reviewer";
    private static final String JWT_ROLE_REVIEWER_ALTERNATIVE = "reviewerAlternative";
    private static final String JWT_ROLE_ADMIN = "admin";
    private static final String JWT_ROLE_WEBCLIENT = "webclient";
    private static final String JWT_ROLE_SURVEY_UNIT = "surveyUnit";

    @BeforeEach
    void init() {
        oidcProperties = new OidcProperties(true, "host", "url", "realm", "principal-attribute", "", "client-id");

    }

    @Test
    @DisplayName("Given a JWT, when converting null or empty JWT role, then converting ignore these roles")
    void testConverter01() {
        RoleProperties roleProperties = new RoleProperties("", null, JWT_ROLE_ADMIN, JWT_ROLE_WEBCLIENT, JWT_ROLE_REVIEWER_ALTERNATIVE, JWT_ROLE_SURVEY_UNIT);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        List<String> tokenRoles = new ArrayList<>();
        tokenRoles.add(null);
        tokenRoles.add("");

        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then convert only JWT roles matching roles in role properties")
    void testConverter02() {
        RoleProperties roleProperties = new RoleProperties(JWT_ROLE_INTERVIEWER, JWT_ROLE_REVIEWER, JWT_ROLE_ADMIN, JWT_ROLE_WEBCLIENT, JWT_ROLE_REVIEWER_ALTERNATIVE, JWT_ROLE_SURVEY_UNIT);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        List<String> tokenRoles = List.of("dummyRole1", roleProperties.reviewer(), "dummyRole2", roleProperties.interviewer(), "dummyRole3", roleProperties.surveyUnit());

        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority(AuthorityRoleEnum.INTERVIEWER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.SURVEY_UNIT.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.REVIEWER.securityRole()));
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then accept a config role can be used for multiple app roles")
    void testConverter03() {
        String dummyRole = "dummyRole";
        String dummyRole2 = "dummyRole2";
        RoleProperties roleProperties = new RoleProperties(dummyRole, dummyRole, dummyRole2, dummyRole2, null, dummyRole2);
        oidcProperties = new OidcProperties(true, "host", "url", "realm", "principal-attribute", "", "client-id");
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);

        List<String> tokenRoles = List.of(dummyRole, "role-not-used", dummyRole2, "role-not-used-2");
        Jwt jwt = createJwt(tokenRoles);

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(5)
                .contains(
                        new SimpleGrantedAuthority(AuthorityRoleEnum.INTERVIEWER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.REVIEWER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.ADMIN.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.WEBCLIENT.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.SURVEY_UNIT.securityRole()));
    }

    @ParameterizedTest
    @MethodSource("provideJWTRoleWithAppRoleAssociated")
    @DisplayName("Given a JWT, when converting roles, then assure each JWT role is converted to equivalent app role")
    void testConverter04(String jwtRole, AuthorityRoleEnum appRole) {
        RoleProperties roleProperties = new RoleProperties(JWT_ROLE_INTERVIEWER, JWT_ROLE_REVIEWER, JWT_ROLE_ADMIN, JWT_ROLE_WEBCLIENT, JWT_ROLE_REVIEWER_ALTERNATIVE, JWT_ROLE_SURVEY_UNIT);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        List<String> tokenRoles = List.of(jwtRole);

        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(1)
                .contains(new SimpleGrantedAuthority(appRole.securityRole()));
    }

    @Test
    @DisplayName("Given a JWT, when role claim is defined, then role claim is used to retrieve roles")
    void testConverter05() {
        oidcProperties = new OidcProperties(true, "host", "url", "realm", "principal-attribute", "roleClaim", "client-id");
        RoleProperties roleProperties = new RoleProperties(JWT_ROLE_INTERVIEWER, JWT_ROLE_REVIEWER, JWT_ROLE_ADMIN, JWT_ROLE_WEBCLIENT, JWT_ROLE_REVIEWER_ALTERNATIVE, JWT_ROLE_SURVEY_UNIT);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        Map<String, Object> claims = new HashMap<>();
        List<String> tokenRoles = List.of(JWT_ROLE_INTERVIEWER, JWT_ROLE_REVIEWER);
        claims.put(oidcProperties.roleClaim(), tokenRoles);
        Map<String, Object> jwtHeaders = new HashMap<>();
        jwtHeaders.put("header", "headerValue");

        Jwt jwt = new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(2)
                .contains(new SimpleGrantedAuthority(AuthorityRoleEnum.INTERVIEWER.securityRole()))
                .contains(new SimpleGrantedAuthority(AuthorityRoleEnum.REVIEWER.securityRole()));
    }

    private static Stream<Arguments> provideJWTRoleWithAppRoleAssociated() {
        return Stream.of(
                Arguments.of(JWT_ROLE_INTERVIEWER, AuthorityRoleEnum.INTERVIEWER),
                Arguments.of(JWT_ROLE_REVIEWER, AuthorityRoleEnum.REVIEWER),
                Arguments.of(JWT_ROLE_REVIEWER_ALTERNATIVE, AuthorityRoleEnum.REVIEWER_ALTERNATIVE),
                Arguments.of(JWT_ROLE_ADMIN, AuthorityRoleEnum.ADMIN),
                Arguments.of(JWT_ROLE_WEBCLIENT, AuthorityRoleEnum.WEBCLIENT),
                Arguments.of(JWT_ROLE_SURVEY_UNIT, AuthorityRoleEnum.SURVEY_UNIT));
    }

    private Jwt createJwt(List<String> tokenRoles) {
        Map<String, Object> jwtHeaders = new HashMap<>();
        jwtHeaders.put("header", "headerValue");

        Map<String, Object> claims = new HashMap<>();
        Map<String, List<String>> realmRoles = new HashMap<>();
        realmRoles.put(GrantedAuthorityConverter.REALM_ACCESS_ROLE, tokenRoles);
        claims.put(GrantedAuthorityConverter.REALM_ACCESS, realmRoles);

        return new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
    }
}
