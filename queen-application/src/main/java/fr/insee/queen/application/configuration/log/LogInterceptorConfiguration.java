package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Add the log interceptor to the app
 */
@Configuration
@RequiredArgsConstructor
public class LogInterceptorConfiguration implements WebMvcConfigurer {
    private final AuthenticationHelper authenticationHelper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.myLogInterceptor(authenticationHelper));
    }

    @Bean
    public LogInterceptor myLogInterceptor(AuthenticationHelper authenticationHelper) {
        return new LogInterceptor(authenticationHelper);
    }
}
