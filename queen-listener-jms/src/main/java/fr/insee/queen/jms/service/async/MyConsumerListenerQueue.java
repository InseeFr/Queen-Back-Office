package fr.insee.queen.jms.service.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import fr.insee.queen.jms.bean.UniteEnquetee;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;

import static fr.insee.queen.jms.configuration.ConfigurationJMS.UE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyConsumerListenerQueue {

    private final CampaignService campaignService;

    private final CampaignExistenceService campaignExistenceService;

    private final QuestionnaireModelService questionnaireModelService;

    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;

    private final SurveyUnitService surveyUnitService;

    //    @JmsListener est la seule annotation requise pour convertir une méthode d'un bean normal en un point de terminaison d'écoute JMS
    @JmsListener(destination = UE_QUEUE, containerFactory = "queueJmsListenerContainerFactory")
    public void queueConnectionFactory(Message message, Session session) throws JMSException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
//        // Convert from json String to POJO
        UniteEnquetee yourPojo = objectMapper.readValue(message.getBody(String.class) , UniteEnquetee.class);

        log.info("Received yourPojo <" + yourPojo.toString() + ">");

        try {

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(yourPojo);

            if(!campaignExistenceService.existsById(yourPojo.getCampagneId())){
                campaignService.createCampaign(new Campaign(yourPojo.getCampagneId(), "Campagne de test JMS", "{}"));
                if(!questionnaireModelExistenceService.existsById(yourPojo.getQuestionnaireId())){
                    QuestionnaireModel qm = QuestionnaireModel.createQuestionnaireWithCampaign(yourPojo.getQuestionnaireId(), "Questionnaire de test JMS", "{}", new HashSet<String>(), yourPojo.getCampagneId());
                    questionnaireModelService.createQuestionnaire(qm);
                }
            }

            surveyUnitService.createSurveyUnit(new SurveyUnit(yourPojo.getExternalId().toString(), yourPojo.getCampagneId(), yourPojo.getQuestionnaireId(), "{\"p1\": \"p1\"}", json, "{\"comment1\": \"comment1\"}", new StateData(StateDataType.INIT, new Date().getTime(), "P1")));

            // Convert from POJO to json String
            ObjectMapper objectMapper2 = new ObjectMapper();
            String ueAsString = objectMapper2.writeValueAsString(yourPojo);

            final ObjectMessage responseMessage = new ActiveMQObjectMessage();
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
            responseMessage.setObject(ueAsString);

            final MessageProducer producer = session.createProducer(message.getJMSReplyTo());

            log.info("Launch to replyTo(queueConnectionFactory)");
            producer.send(responseMessage);
            log.info(yourPojo.toString());

        } catch (StateDataInvalidDateException e) {
            throw new RuntimeException(e);
        }
    }
}