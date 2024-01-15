package fr.insee.queen.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API HealthCheck
 */
@RestController
@Tag(name = "09. HealthCheck")
@RequestMapping(path = "/api")
@Slf4j
public class HealthCheckController {

    @Operation(summary = "Healthcheck, check if api is alive")
    @GetMapping(path = "/healthcheck")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
        log.debug("HealthCheck");
    }
}
