package fr.insee.queen.jms.service.stub;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationBatchException;
import fr.insee.queen.jms.exception.SchemaValidationException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Set;

public class InterrogationBatchFakeService implements InterrogationBatchService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    public static final String INTERROGATION_BATCH_EXCEPTION = "InterrogationBatchException exception";

    public static final String SCHEMA_VALIDATION_EXCEPTION = "SchemaValidationException exception";

    public static final String ENTITY_NOT_FOUND_EXCEPTION = "EntityNotFoundException exception";

    @Getter
    private Interrogation interrogationBatchUsed = null;

    @Setter
    private boolean shouldThrowInterrogationBatchException = false;

    @Setter
    private boolean shouldThrowSchemaValidationException = false;

    @Setter
    private boolean shouldThrowEntityNotFoundException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;

    @SneakyThrows
    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        if(shouldThrowInterrogationBatchException) {
            throw new InterrogationBatchException(INTERROGATION_BATCH_EXCEPTION);
        }

        if(shouldThrowSchemaValidationException) {
            throw new SchemaValidationException(SCHEMA_VALIDATION_EXCEPTION, Set.of());
        }

        if(shouldThrowEntityNotFoundException) {
            throw new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION);
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }
        interrogationBatchUsed = Interrogation.create(interrogations.getFirst().id(),
                interrogations.getFirst().surveyUnitId(),
                interrogations.getFirst().personalization(),
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
