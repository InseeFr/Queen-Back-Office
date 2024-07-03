package fr.insee.queen.jms.service.stub;

import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.InterrogationCommandService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import lombok.Getter;
import lombok.Setter;

public class InterrogationCommandFakeService implements InterrogationCommandService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    @Getter
    private InterrogationCommand interrogationCommandUsed = null;

    @Setter
    private boolean shouldThrowInterrogationCommandException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;

    @Override
    public void createInterrogation(InterrogationCommand interrogationCommand) throws InterrogationCommandException {
        if(shouldThrowInterrogationCommandException) {
            throw new InterrogationCommandException(interrogationCommand.questionnaireId());
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }
        interrogationCommandUsed = interrogationCommand;
    }
}
