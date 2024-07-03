package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;

public interface InterrogationCommandService {
    /**
     * create a survey unit from a command
     * @param interrogationCommand Command for survey unit creation
     * @throws InterrogationCommandException exception thrown when command failed
     */
    void createInterrogation(InterrogationCommand interrogationCommand) throws InterrogationCommandException;
}
