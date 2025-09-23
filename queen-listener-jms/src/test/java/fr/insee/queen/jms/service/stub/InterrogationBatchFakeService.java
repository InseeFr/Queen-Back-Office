package fr.insee.queen.jms.service.stub;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class InterrogationBatchFakeService implements InterrogationBatchService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    @Getter
    private Interrogation interrogationBatchUsed = null;

    @Setter
    private boolean shouldThrowInterrogationBatchException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;


    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        if(shouldThrowInterrogationBatchException) {
//            TODO
            throw new InterrogationCommandException("");
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }
        interrogationBatchUsed = interrogations.getFirst();
    }

    @Override
    public void delete(List<String> interrogationIds) {
        // TODO
    }
}
