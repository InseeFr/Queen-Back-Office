package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.*;
import fr.insee.queen.domain.surveyunit.service.exception.SurveyUnitCommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitCommandServiceImpl implements SurveyUnitCommandService {
    private final SurveyUnitRepository surveyUnitRepository;
    private final CampaignService campaignService;

    @Transactional
    @Override
    public void createSurveyUnit(SurveyUnitCommand surveyUnitCommand) throws SurveyUnitCommandException {
        Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.find(surveyUnitCommand.id());
        if(surveyUnitOptional.isEmpty()) {
            String campaignId = campaignService
                    .findCampaignIdFromQuestionnaireId(surveyUnitCommand.questionnaireId())
                    .orElseThrow(() -> new SurveyUnitCommandException(surveyUnitCommand.questionnaireId()));
            SurveyUnit surveyUnitToCreate = new SurveyUnit(surveyUnitCommand.id(),
                    campaignId,
                    surveyUnitCommand.questionnaireId(),
                    surveyUnitCommand.personalization(),
                    surveyUnitCommand.data(),
                    null,
                    null,
                    surveyUnitCommand.correlationId());
            surveyUnitRepository.create(surveyUnitToCreate);
            return;
        }
        SurveyUnit surveyUnit = surveyUnitOptional.get();
        if(!surveyUnitCommand.correlationId().equals(surveyUnit.correlationId())) {
            throw new SurveyUnitCommandException(surveyUnit.correlationId(), surveyUnitCommand.correlationId());
        }
    }
}
