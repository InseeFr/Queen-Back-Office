package fr.insee.queen.jms.service.stub;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationBatchException;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class InterrogationBatchFakeService implements InterrogationBatchService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    public static final String INTERROGATION_BATCH_EXCEPTION = "InterrogationBatchException exception";

    @Getter
    private Interrogation interrogationBatchUsed = null;

    @Setter
    private boolean shouldThrowInterrogationBatchException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;


    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        if(shouldThrowInterrogationBatchException) {
            throw new InterrogationBatchException(INTERROGATION_BATCH_EXCEPTION);
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }
        interrogationBatchUsed = Interrogation.create(interrogations.getFirst().id(),
                interrogations.getFirst().surveyUnitId(),
                interrogations.getFirst().personalization(),
                interrogations.getFirst().comment(),
                interrogations.getFirst().data(),
                interrogations.getFirst().stateData());
    }

    @Override
    public void saveInterrogation(Interrogation interrogation) {
        this.saveInterrogations(List.of(interrogation));
    }

    @Override
    public void delete(List<String> interrogationIds) {
        // TODO
    }
}
