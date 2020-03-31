package fr.insee.queen.api.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import springfox.documentation.PathProvider;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* SwaggerConfiguration is the class using to configure swagger
* 3 ways to authenticated : 
* 	- without authentication,
* 	- basic authentication 
* 	- and keycloak authentication 
* 
* @author Claudel Benjamin
* 
*/
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	/**
	 * The name of Spring application.<br>
	 * Generate with the application property spring.application.name
	 */
    @Value("${spring.application.name}")
    private String name;
    
    @Bean
    public Docket api() {
    	List<ResponseMessage> messages = new ArrayList<ResponseMessage>();
    	messages.add(new ResponseMessageBuilder().code(200).message("Success!").build());
    	messages.add(new ResponseMessageBuilder().code(401).message("Not authorized!").build());
    	messages.add(new ResponseMessageBuilder().code(403).message("Forbidden!").build());
    	messages.add(new ResponseMessageBuilder().code(404).message("Not found!").build());
    	String urlString = "/" + name;
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
        		.useDefaultResponseMessages(false)
        		.globalResponseMessage(RequestMethod.GET, messages)
        		.globalResponseMessage(RequestMethod.POST, messages)
        		.globalResponseMessage(RequestMethod.PUT, messages)
                .apiInfo(apiInfo())
                .ignoredParameterTypes(HttpServletRequest.class, HttpServletResponse.class, HttpSession.class)
                .alternateTypeRules(AlternateTypeRules.newRule(StreamingResponseBody.class, MultipartFile.class));
        try {
            URL url = new URL(urlString);
            docket = docket.pathMapping(url.getPath()).host(url.getHost() + (url.getPort() > -1 ? ":" + url.getPort() : ""));
        } catch (MalformedURLException e) {
            docket = docket.pathMapping(urlString);
        }
        	docket
                .pathProvider(absolutePathProvider())
                .securitySchemes(List.of(new BasicAuth(name)))
                .securityContexts(List.of(SecurityContext.builder()
        		.securityReferences(List.of(new SecurityReference(name, new AuthorizationScope[0])))
        		.forPaths(PathSelectors.regex("/.*")).build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/.*"))
                .build();
        return docket;
       
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
    
    private ApiInfo apiInfo() {
        return new ApiInfo("Queen-Back-Office", "Back-office services for Queen", "1.0", "", new Contact("Metallica", "", "https://github.com/InseeFr/Queen-Back-Office"), "LICENSEE", "https://github.com/InseeFr/Queen-Back-Office/blob/master/LICENSE", List.of());
    }
}