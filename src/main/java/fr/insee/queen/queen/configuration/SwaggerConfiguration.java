package fr.insee.queen.queen.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import springfox.documentation.PathProvider;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
   
    @Bean
    public Docket api() {
        String urlString = "/queen";
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .ignoredParameterTypes(HttpServletRequest.class, HttpServletResponse.class, HttpSession.class)
                .alternateTypeRules(AlternateTypeRules.newRule(StreamingResponseBody.class, MultipartFile.class));
        try {
            URL url = new URL(urlString);
            docket = docket.pathMapping(url.getPath()).host(url.getHost() + (url.getPort() > -1 ? ":" + url.getPort() : ""));
        } catch (MalformedURLException e) {
            docket = docket.pathMapping(urlString);
        }
        return docket
                .pathProvider(absolutePathProvider())
                .securitySchemes(List.of(new BasicAuth("queen")))
                .securityContexts(List.of(SecurityContext.builder()
                		.securityReferences(List.of(new SecurityReference("queen", new AuthorizationScope[0])))
                		.forPaths(PathSelectors.regex("/.*")).build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api/.*"))
                .build();
    }
    private PathProvider absolutePathProvider() {
        return new AbstractPathProvider() {
            @Override
            protected String getDocumentationPath() {
                return "";
            }
            @Override
            protected String applicationPath() {
                return "";
            }
        };
    }
    
    // TODO : to complete
    private ApiInfo apiInfo() {
        return new ApiInfo("Queen", "queen", "test", "benjamin.claudel@keyconsulting.fr", new Contact("", "", ""), "", "", List.of());
    }
}