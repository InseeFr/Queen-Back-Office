package fr.insee.queen.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.insee.queen.api.repository.OperationRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = OperationRepository.class)
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
