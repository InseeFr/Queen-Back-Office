package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.group.service.GroupExistenceService;
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
    private final GroupExistenceService groupExistenceService;

    @Transactional
    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        Interrogation interrogation = interrogations.getFirst();
        groupExistenceService.throwExceptionIfGroupNotLinkedToQuestionnaire(interrogation.groupId(), interrogation.questionnaireId());
        batchRepository.upsertAll(interrogations);
    }

    @Transactional
    @Override
    public void saveInterrogation(@NonNull Interrogation interrogation) {
        saveInterrogations(List.of(interrogation));
    }

    @Transactional
    @Override
    public void delete(@NonNull List<String> interrogationIds) {
        batchRepository.deleteAll(interrogationIds);
    }
}