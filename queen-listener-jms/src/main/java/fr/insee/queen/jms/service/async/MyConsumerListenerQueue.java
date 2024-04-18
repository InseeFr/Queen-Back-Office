package fr.insee.queen.jms.service.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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

    //    @JmsListener est la seule annotation requise pour convertir une méthode d'un bean normal en un point de terminaison d'écoute JMS
    @JmsListener(destination = UE_QUEUE, containerFactory = "myFactory")
    public void queueConnectionFactory(Message message, Session session) throws JMSException, JsonProcessingException {
        JsonNode mySurveyUnit = objectMapper.readTree(message.getBody(String.class));
        String campagneId = "BBC2023A00";
        String questionnaireId = mySurveyUnit.path("payload").path("_children").path("questionnaireId").get("_value").textValue();
        String mySurveyUnitId = mySurveyUnit.path("payload").path("_children").path("id").get("_value").textValue();

        try {

//            TODO : en mode TT
            if(!campaignExistenceService.existsById(campagneId))
            {
                campaignService.createCampaign(new Campaign(campagneId, "Campagne de test JMS", "{}"));
            }
            if(!questionnaireModelExistenceService.existsById(questionnaireId))
            {
                QuestionnaireModel qm = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, questionnaireId, "{}", new HashSet<String>(), campagneId);
                questionnaireModelService.createQuestionnaire(qm);
            }
            if(!surveyUnitService.existsById(mySurveyUnitId)) {
                surveyUnitService.createSurveyUnit(new SurveyUnit(mySurveyUnitId, campagneId, questionnaireId, "{\"p1\": \"p1\"}", message.getBody(String.class), "{\"comment1\": \"comment1\"}", new StateData(StateDataType.INIT, new Date().getTime(), "P1")));
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