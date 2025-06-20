package fr.insee.queen.application.events.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.service.EventsService;
import fr.insee.queen.domain.paradata.service.ParadataEventService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public void addEvents(@NotNull @RequestBody ObjectNode event){
        eventsService.createEvent(event);
    }

}
