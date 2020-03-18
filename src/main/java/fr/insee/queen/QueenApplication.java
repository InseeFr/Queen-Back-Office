package fr.insee.queen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.insee.queen.repository.OperationRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = OperationRepository.class)
public class QueenApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueenApplication.class, args);
	}

}
