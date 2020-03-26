package fr.insee.queen.api;
import java.net.URISyntaxException;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
public abstract class AbstractTests {
    @Autowired
    private TestRestTemplate restTemplate;
    private HttpHeaders headers;
    @BeforeEach
    public void clearSession() {
    	this.headers = null;
    }
    public <T> ResponseEntity<T> get(String uri, Class<T> clazz) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, clazz);
    }
    public <T, P> ResponseEntity<T> put(String uri, P o, Class<T> clazz) {
        HttpEntity<P> requestEntity = new HttpEntity<>(o, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, clazz);
    }
    public <T,P> ResponseEntity<T> post(String uri, P o, Class<T> clazz) {
        HttpEntity<P> requestEntity = new HttpEntity<>(o, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, clazz);
    }
	protected ResponseEntity<byte[]> getFile(String uri) {
	    restTemplate.getRestTemplate().getMessageConverters().add(new ByteArrayHttpMessageConverter());
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    return restTemplate.exchange(uri, HttpMethod.GET, entity, byte[].class);
	}
    public <T,P> ResponseEntity<T> postFile(String uri, String ressourcePath, Class<T> clazz) throws URISyntaxException {
    	var map = new LinkedMultiValueMap<String, Object>();
	    map.add("data", new ClassPathResource(ressourcePath));
	    try {
		    headers.add("content-type", MediaType.MULTIPART_FORM_DATA_VALUE);
		    var requestEntity = new HttpEntity<>(map, headers);
		    return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, clazz);
	    } finally {
			headers.remove("content-type");
		}
    }
    public <T> ResponseEntity<T> delete(String uri, Class<T> clazz) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, clazz);
    }
    public void login() {
        login("admin");
    }
    public void login(String login) {
        headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((login + ":a").getBytes()));
    }

}