package fr.insee.queen.application.crossenvironmentcommunication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.broker.dto.EventDto;
import fr.insee.queen.infrastructure.broker.dto.EventPayloadDto;
import fr.insee.queen.infrastructure.broker.dto.ModeDto;
import fr.insee.queen.domain.event.service.EventService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventsControllerTest {

    private EventsController controller;
    private EventService eventService;
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        objectMapper = new ObjectMapper();
        controller = new EventsController(eventService, objectMapper);

        // Initialize validator for @Valid testing
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("On creating event when event is valid then save is triggered")
    void testAddEvent_Success() {
        // given
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_INIT,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                new EventPayloadDto()
        );

        // when
        controller.addEvent(event);

        // then
        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(eventService, times(1)).saveEvent(captor.capture());

        ObjectNode savedEvent = captor.getValue();
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.get("eventType").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(savedEvent.get("aggregateType").asText()).isEqualTo("QUESTIONNAIRE");
    }

    @Test
    @DisplayName("On creating event when eventType is missing then validation fails")
    void testAddEvent_MissingEventType() {
        // given
        EventDto event = new EventDto(
                null,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                new EventPayloadDto()
        );

        // when
        Set<ConstraintViolation<EventDto>> violations = validator.validate(event);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("eventType"));
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    @DisplayName("On creating event when aggregateType is missing then validation fails")
    void testAddEvent_MissingAggregateType() {
        // given
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_INIT,
                null,
                new EventPayloadDto()
        );

        // when
        Set<ConstraintViolation<EventDto>> violations = validator.validate(event);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("aggregateType"));
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    @DisplayName("On creating event when payload is missing then validation fails")
    void testAddEvent_MissingPayload() {
        // given
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_INIT,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                null
        );

        // when
        Set<ConstraintViolation<EventDto>> violations = validator.validate(event);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("payload"));
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    @DisplayName("On creating event when all fields are valid then no violations")
    void testAddEvent_AllFieldsValid() {
        // given
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_INIT,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                new EventPayloadDto("1", ModeDto.CAPI)
        );

        // when
        Set<ConstraintViolation<EventDto>> violations = validator.validate(event);

        // then
        assertThat(violations).isEmpty();
    }
}