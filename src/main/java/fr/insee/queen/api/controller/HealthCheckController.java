package fr.insee.queen.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* HealthCheck is the Controller using to check if API is alive
* 
* @author Laurent Caouissin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
public class HealthCheckController {

	@Operation(summary = "Healthcheck, check if api is alive")
	@GetMapping(path = "/healthcheck")
	public ResponseEntity<Object> healthCheck() {
		log.debug("HealthCheck");
		return ResponseEntity.ok().build();
	}
}
