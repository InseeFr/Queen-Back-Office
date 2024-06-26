package fr.insee.queen.application.paradata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.paradata.service.dummy.ParadataEventFakeService;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ParadataEventControllerTest {

    private ParadataEventFakeService paradataEventService;

    private PilotageFakeComponent pilotageComponent;

    private ParadataEventController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        paradataEventService = new ParadataEventFakeService();
        pilotageComponent = new PilotageFakeComponent();
        controller = new ParadataEventController(paradataEventService, pilotageComponent);
    }

    @Test
    @DisplayName("On creating paradata when paradata ok save call is triggered")
    void addParadata01() throws JsonProcessingException {
        ObjectNode paradata = mapper.readValue("""
                {"idSU": "11", "object": {}}
                """, ObjectNode.class);
        controller.addParadata(paradata);
        assertThat(pilotageComponent.isChecked()).isTrue();
        assertThat(paradataEventService.isCreated()).isTrue();
    }

    @Test
    @DisplayName("On creating paradata when paradata has no su id then throw exception")
    void addParadata02() throws JsonProcessingException {
        ObjectNode paradata = mapper.readValue("""
                {"idU": "11", "object": {}}
                """, ObjectNode.class);
        assertThatThrownBy(() -> controller.addParadata(paradata)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("On creating paradata when paradata su id is not a text value then throw exception")
    void addParadata03() throws JsonProcessingException {
        ObjectNode paradata = mapper.readValue("""
                {"idSU": {}, "object": {}}
                """, ObjectNode.class);
        assertThatThrownBy(() -> controller.addParadata(paradata)).isInstanceOf(EntityNotFoundException.class);
    }
}
