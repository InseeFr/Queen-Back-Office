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

    private Map<String, Object> jwtHeaders;

    private static final String jwtRoleInterviewer = "interviewer";
    private static final String jwtRoleReviewer = "reviewer";
    private static final String jwtRoleReviewerAlternative = "reviewerAlternative";
    private static final String jwtRoleAdmin = "admin";
    private static final String jwtRoleWebclient = "webclient";
    private static final String jwtRoleSurveyUnit = "surveyUnit";

    @BeforeEach
    void init() {
        oidcProperties = new OidcProperties(true, "host", "url", "realm", "principal-attribute", "roleClaim", "client-id");
        jwtHeaders = new HashMap<>();
        jwtHeaders.put("header", "headerValue");
    }

    @Test
    @DisplayName("Given a JWT, when converting null or empty JWT role, then converting ignore these roles")
    void testConverter01() {
        RoleProperties roleProperties = new RoleProperties("", null, jwtRoleAdmin, jwtRoleWebclient, jwtRoleReviewerAlternative, jwtRoleSurveyUnit);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        Map<String, Object> claims = new HashMap<>();
        List<String> tokenRoles = new ArrayList<>();
        tokenRoles.add(null);
        tokenRoles.add("");
        claims.put(oidcProperties.roleClaim(), tokenRoles);


        Jwt jwt = new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then convert only JWT roles matching roles in role properties")
    void testConverter02() {
        RoleProperties roleProperties = new RoleProperties(jwtRoleInterviewer, jwtRoleReviewer, jwtRoleAdmin, jwtRoleWebclient, jwtRoleReviewerAlternative, jwtRoleSurveyUnit);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        Map<String, Object> claims = new HashMap<>();
        List<String> tokenRoles = List.of("dummyRole1", roleProperties.reviewer(), "dummyRole2", roleProperties.interviewer(), "dummyRole3", roleProperties.surveyUnit());
        claims.put(oidcProperties.roleClaim(), tokenRoles);

        Jwt jwt = new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.INTERVIEWER),
                        new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.SURVEY_UNIT),
                        new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.REVIEWER));
    }

    @ParameterizedTest
    @MethodSource("provideJWTRoleWithAppRoleAssociated")
    @DisplayName("Given a JWT, when converting roles, then assure each JWT role is converted to equivalent app role")
    void testConverter03(String jwtRole, AuthorityRoleEnum appRole) {
        RoleProperties roleProperties = new RoleProperties(jwtRoleInterviewer, jwtRoleReviewer, jwtRoleAdmin, jwtRoleWebclient, jwtRoleReviewerAlternative, jwtRoleSurveyUnit);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        Map<String, Object> claims = new HashMap<>();
        List<String> tokenRoles = List.of(jwtRole);
        claims.put(oidcProperties.roleClaim(), tokenRoles);

        Jwt jwt = new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(1)
                .contains(new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + appRole));
    }

    @Test
    @DisplayName("Given a JWT, when no role claim is defined, then default role claim is used")
    void testConverter04() {
        oidcProperties = new OidcProperties(true, "host", "url", "realm", "principal-attribute", "", "client-id");
        RoleProperties roleProperties = new RoleProperties(jwtRoleInterviewer, jwtRoleReviewer, jwtRoleAdmin, jwtRoleWebclient, jwtRoleReviewerAlternative, jwtRoleSurveyUnit);
        converter = new GrantedAuthorityConverter(oidcProperties, roleProperties);
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> roleClaims = new HashMap<>();
        List<String> tokenRoles = List.of(jwtRoleInterviewer, jwtRoleReviewer);
        roleClaims.put(GrantedAuthorityConverter.ROLES, tokenRoles);
        claims.put(GrantedAuthorityConverter.REALM_ACCESS, roleClaims);

        Jwt jwt = new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(2)
                .contains(new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.INTERVIEWER))
                .contains(new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.REVIEWER));
    }

    private static Stream<Arguments> provideJWTRoleWithAppRoleAssociated() {
        return Stream.of(
                Arguments.of(jwtRoleInterviewer, AuthorityRoleEnum.INTERVIEWER),
                Arguments.of(jwtRoleReviewer, AuthorityRoleEnum.REVIEWER),
                Arguments.of(jwtRoleReviewerAlternative, AuthorityRoleEnum.REVIEWER_ALTERNATIVE),
                Arguments.of(jwtRoleAdmin, AuthorityRoleEnum.ADMIN),
                Arguments.of(jwtRoleWebclient, AuthorityRoleEnum.WEBCLIENT),
                Arguments.of(jwtRoleSurveyUnit, AuthorityRoleEnum.SURVEY_UNIT));
    }
}
