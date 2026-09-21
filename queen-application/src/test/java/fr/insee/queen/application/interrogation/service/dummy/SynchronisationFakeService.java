package fr.insee.queen.application.interrogation.service.dummy;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.synchronisation.service.SynchronisationService;
import lombok.Getter;

public class SynchronisationFakeService implements SynchronisationService {

    @Getter
    private String synchronisedInterrogationId = null;

    @Override
    public Interrogation synchronise(String interrogationId) {
        this.synchronisedInterrogationId = interrogationId;
        return new Interrogation(
                interrogationId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public void reset() {
        this.synchronisedInterrogationId = null;
    }
}
