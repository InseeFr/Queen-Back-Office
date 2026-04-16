package fr.insee.queen.application.configuration.registre;

import fr.insee.queen.application.configuration.registre.auth.RegistreAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RegistreRestClientConfig {

    @Bean
    @ConditionalOnProperty(name = "feature.registre.enabled", havingValue = "true")
    public RestClient registreRestClient(RegistreAuthService authService) {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .requestInterceptor((request, body, execution) -> {
                String token = authService.getAccessToken();
                request.getHeaders().setBearerAuth(token);
                return execution.execute(request, body);
            })
            .build();
    }

    @Bean
    @ConditionalOnProperty(name = "feature.registre.enabled", havingValue = "false", matchIfMissing = true)
    public RestClient registreRestClientNoAuth() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}