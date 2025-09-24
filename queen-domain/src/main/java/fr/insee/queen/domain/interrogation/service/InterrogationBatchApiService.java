package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.gateway.InterrogationBatchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class InterrogationBatchApiService implements InterrogationBatchService {

    private final InterrogationBatchRepository batchRepository;
    private final CampaignExistenceService campaignExistenceService;

    @Transactional
    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        Interrogation interrogation = interrogations.getFirst();
        campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(interrogation.campaignId(), interrogation.questionnaireId());
        batchRepository.upsertAll(interrogations);
    }

    @Transactional
    @Override
    public void delete(@NonNull List<String> interrogationIds) {
        batchRepository.deleteAll(interrogationIds);
    }
}