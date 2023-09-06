package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.RoleProperties;
import fr.insee.queen.api.constants.Constants;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Handle http requests authorization when app use keycloak/basic authentication
 */
@Component
public class ApiRequestsAuthorizer {
    private final RoleProperties roleProperties;
    private final ApplicationProperties applicationProperties;

    public ApiRequestsAuthorizer(ApplicationProperties applicationProperties, RoleProperties roleProperties) {
        this.roleProperties = roleProperties;
        this.applicationProperties = applicationProperties;
    }

    /**
     *
     * @param configurer configuration object used to configure the http requests authorization
     * @return the configuration object with http requests authorization configured
     */
    public AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry handleApiRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) {
        String roleAdmin = roleProperties.admin();
        String roleInterviewer = roleProperties.interviewer();
        String roleWebClient = roleProperties.webclient();
        String roleReviewer = roleProperties.reviewer();
        String roleReviewerAlternative = roleProperties.reviewerAlternative();

        return configurer
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers(applicationProperties.publicUrls()).permitAll()
                // healtcheck
                .requestMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
                // actuator (actuator metrics are disabled by default)
                .requestMatchers(HttpMethod.GET, Constants.API_ACTUATOR).permitAll()

                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS) .hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_ADMIN_CAMPAIGNS).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST, Constants.API_CAMPAIGNS).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.DELETE, Constants.API_CAMPAIGN_ID).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST, Constants.API_CAMPAIGN_CONTEXT).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SURVEY_UNITS).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.POST, Constants.API_CAMPAIGN_ID_SURVEY_UNIT).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_METADATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_QUESTIONAIRES).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_QUESTIONAIREID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_REQUIREDNOMENCLATURES).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS_STATEDATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer)
                .requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.DELETE, Constants.API_SURVEYUNIT_ID).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID_TEMP_ZONE).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_TEMP_ZONE).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_DATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_DATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_COMMENT).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_COMMENT).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_STATEDATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_STATEDATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS_QUESTIONNAIRE_MODEL_ID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_DEPOSITPROOF).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_PERSONALIZATION).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_PERSONALIZATION).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_NOMENCLATURES).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST, Constants.API_NOMENCLATURE).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_NOMENCLATURE_ID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_QUESTIONNAIRE_ID).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_QUESTIONNAIRE_ID_METADATA).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.GET, Constants.API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer, roleReviewer,roleReviewerAlternative)
                .requestMatchers(HttpMethod.POST, Constants.API_QUESTIONNAIREMODELS).hasAnyRole(roleAdmin, roleWebClient)
                .requestMatchers(HttpMethod.POST,Constants.API_PARADATAEVENT).hasAnyRole(roleAdmin, roleWebClient, roleInterviewer)
                .requestMatchers(HttpMethod.POST, Constants.API_CREATE_DATASET).hasAnyRole(roleAdmin, roleWebClient)
                .anyRequest().denyAll();
    }
}
