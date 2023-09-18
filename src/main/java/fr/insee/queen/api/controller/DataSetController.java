package fr.insee.queen.api.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.service.DataSetInjectorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Profile(value = {"dev", "test"})
public class DataSetController {
	private final DataSetInjectorService injector;

	@Operation(summary = "Create dataset")
	@PostMapping(path = "/create-dataset")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public ResponseEntity<String> createDataSet() {
		injector.createDataSet();
		log.info("Dataset creation end");
		return new ResponseEntity<>("dataSet created", HttpStatus.OK);
	}

}
