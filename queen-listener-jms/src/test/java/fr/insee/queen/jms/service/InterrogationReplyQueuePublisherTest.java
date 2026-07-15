package fr.insee.queen.jms.service;

import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jms.core.JmsTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class InterrogationReplyQueuePublisherTest {

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private JmsTemplate jmsQueuePublisher;

    @InjectMocks
    private InterrogationReplyQueuePublisher surveyUnitReplyQueuePublisher;

    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.US);
        surveyUnitReplyQueuePublisher = new InterrogationReplyQueuePublisher(jsonMapper, jmsQueuePublisher);
    }

    @Test
    @DisplayName("Should send message successfully")
    void shouldLogInfoAndSendMessageSuccessfully(CapturedOutput output) throws Exception {
        // Given
        String replyQueue = "replyQueue";
        String correlationId = "12345";
        ResponseCode responseCode = ResponseCode.CREATED;
        JMSOutputMessage responseMessage = JMSOutputMessage.createResponse(responseCode);
        String jsonResponse = "{\"code\":\""
                + responseCode.getCode() + "\",\"message\":\""
                + responseCode.name() + "\"}";
        when(jsonMapper.writeValueAsString(responseMessage)).thenReturn(jsonResponse);

        // When
        surveyUnitReplyQueuePublisher.send(replyQueue, correlationId, responseMessage);

        // Then
        assertThat(output).contains("Command '" + correlationId + "' - sent");
        verify(jmsQueuePublisher).send(eq(replyQueue), any());
    }

    @Test
    @DisplayName("Should log error when JsonProcessingException occurs")
    void shouldLogErrorWhenJsonProcessingExceptionOccurs(CapturedOutput output) throws Exception {
        // Given
        String replyQueue = "replyQueue";
        String correlationId = "12345";
        JMSOutputMessage responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);
        when(jsonMapper.writeValueAsString(responseMessage)).thenThrow(JacksonException.class);

        // When
        surveyUnitReplyQueuePublisher.send(replyQueue, correlationId, responseMessage);

        // Then
        assertThat(output).contains("Command '" + correlationId + "' - Unable to process json response");
        // Ensure that the message was not sent because of the exception
        verify(jmsQueuePublisher, never()).send(anyString(), any());
    }
}