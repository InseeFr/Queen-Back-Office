package fr.insee.queen.application.events.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.service.EventsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@ConditionalOnProperty(name = "feature.cross-environnement-communication.endpoint", havingValue = "true")
@RestController
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventsController {

    private final EventsService eventsService;

    @Operation(summary = "Create an events")
    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEvent(@NotNull @RequestBody ObjectNode event){
        eventsService.createEvent(event);
    }

}
