package fr.insee.queen.jms.service.async;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelExistenceService;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.jms.configuration.ConsumerProperties;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.insee.queen.jms.configuration.ConfigurationJMS.UE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyConsumerListenerQueue {

    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
    private final CampaignService campaignService;
    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelService questionnaireModelService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final SurveyUnitService surveyUnitService;
    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private JmsTemplate JmsTemplateQueue;
    @Autowired
    ConsumerProperties consumerProperties;

    //    @JmsListener est la seule annotation requise pour convertir une méthode d'un bean normal en un point de terminaison d'écoute JMS
    @JmsListener(destination = UE_QUEUE, containerFactory = "myFactory")
    public void queueConnectionFactory(Message message, Session session) throws JMSException, JsonProcessingException {
        JsonNode mySurveyUnit = objectMapper.readTree(message.getBody(String.class));
        JsonNode myPayload = objectMapper.readTree(mySurveyUnit.get("payload").asText());
        String campagneId = mySurveyUnit.get("CampaignID").asText();
        String questionnaireId = mySurveyUnit.get("questionnaireID").asText();
        String mySurveyUnitId = myPayload.get("repositoryId").asText();
        log.debug("mySurveyUnitId : {}", mySurveyUnitId);
        try {
            /*
            Début de Mock
            fr.insee.mock.mockcreatecampaignandquestionnaire
             */
            if(consumerProperties.isMockcreatecampaignandquestionnaire() && !campaignExistenceService.existsById(campagneId))
            {
                campaignService.createCampaign(new Campaign(campagneId, "Campagne de test JMS", JsonNodeFactory.instance.objectNode()));
            }
            if(consumerProperties.isMockcreatecampaignandquestionnaire() && !questionnaireModelExistenceService.existsById(questionnaireId))
            {
                QuestionnaireModel qm = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, questionnaireId, JsonNodeFactory.instance.objectNode(), new HashSet<String>(), campagneId);
                questionnaireModelService.createQuestionnaire(qm);
            }
            /*
            Fin de Mock
             */
            if(!surveyUnitService.existsById(mySurveyUnitId)) {
                ObjectNode json = (ObjectNode) new ObjectMapper().readTree(message.getBody(String.class));

                surveyUnitService.createSurveyUnit(new SurveyUnit(mySurveyUnitId, campagneId, questionnaireId, new ObjectMapper().createArrayNode(), json, JsonNodeFactory.instance.objectNode(), new StateData(StateDataType.INIT, new Date().getTime(), "P1")));
                this.sendWithReplyQueue(mySurveyUnit);
            }
        } catch (StateDataInvalidDateException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendWithReplyQueue(JsonNode ue) {
        log.debug("UPDATE to MongoDB - sendWithReplyQueue - "+ue.get("replyTo").textValue()+" - "+ ue);
        JmsTemplateQueue.send(ue.get("replyTo").textValue(), session -> {
            try {
                // Convert from POJO to json String
                String ueAsString = objectMapper.writeValueAsString(ue);

                ObjectMessage objectMessage = session.createObjectMessage(ueAsString);
                objectMessage.setJMSCorrelationID(ue.get("correlationID").textValue());
                objectMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

                log.debug("sendWithReplyQueue - Launch to send()");
                return objectMessage;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}