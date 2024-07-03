package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterrogationCommandServiceImpl implements InterrogationCommandService {
    private final InterrogationRepository interrogationRepository;
    private final CampaignService campaignService;

    @Transactional
    @Override
    public void createInterrogation(InterrogationCommand interrogationCommand) throws InterrogationCommandException {
        Optional<Interrogation> interrogationOptional = interrogationRepository.find(interrogationCommand.id());
        if(interrogationOptional.isEmpty()) {
            String campaignId = campaignService
                    .findCampaignIdFromQuestionnaireId(interrogationCommand.questionnaireId())
                    .orElseThrow(() -> new InterrogationCommandException(interrogationCommand.questionnaireId()));
            Interrogation interrogationToCreate = new Interrogation(interrogationCommand.id(),
                    interrogationCommand.surveyUnitId(),
                    campaignId,
                    interrogationCommand.questionnaireId(),
                    interrogationCommand.personalization(),
                    interrogationCommand.data(),
                    JsonNodeFactory.instance.objectNode(),
                    null,
                    interrogationCommand.correlationId());
            interrogationRepository.create(interrogationToCreate);
            return;
        }
        Interrogation interrogation = interrogationOptional.get();
        if(!interrogationCommand.correlationId().equals(interrogation.correlationId())) {
            throw new InterrogationCommandException(interrogation.correlationId(), interrogationCommand.correlationId());
        }
    }
}
