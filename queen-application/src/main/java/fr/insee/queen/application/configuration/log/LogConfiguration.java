package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.configuration.auth.CustomAccessDeniedHandler;
import fr.insee.queen.application.configuration.auth.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor
public class LogConfiguration {

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint(JsonMapper mapper,
                                                                   LogInterceptor logInterceptor) {
        return new CustomAuthenticationEntryPoint(logInterceptor, mapper);
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(JsonMapper mapper,
                                                         LogInterceptor logInterceptor) {
        return new CustomAccessDeniedHandler(mapper, logInterceptor);
    }
}
