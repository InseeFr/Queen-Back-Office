package fr.insee.queen.jms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(OutputCaptureExtension.class)
class InterrogationReplyQueuePublisherTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JmsTemplate jmsQueuePublisher;

    @InjectMocks
    private InterrogationReplyQueuePublisher surveyUnitReplyQueuePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        surveyUnitReplyQueuePublisher = new InterrogationReplyQueuePublisher(objectMapper, jmsQueuePublisher);
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
        when(objectMapper.writeValueAsString(responseMessage)).thenReturn(jsonResponse);

        // When
        surveyUnitReplyQueuePublisher.send(replyQueue, correlationId, responseMessage);

        // Then
        assertThat(output).contains("Command " + correlationId + " - sent");
        verify(jmsQueuePublisher).send(eq(replyQueue), any());
    }

    @Test
    @DisplayName("Should log error when JsonProcessingException occurs")
    void shouldLogErrorWhenJsonProcessingExceptionOccurs(CapturedOutput output) throws Exception {
        // Given
        String replyQueue = "replyQueue";
        String correlationId = "12345";
        JMSOutputMessage responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);
        when(objectMapper.writeValueAsString(responseMessage)).thenThrow(JsonProcessingException.class);

        // When
        surveyUnitReplyQueuePublisher.send(replyQueue, correlationId, responseMessage);

        // Then
        assertThat(output).contains("Command " + correlationId + " - Unable to process json response");
        // Ensure that the message was not sent because of the exception
        verify(jmsQueuePublisher, never()).send(anyString(), any());
    }
}