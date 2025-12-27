package fr.insee.queen.application.interrogation.service.dummy;

import fr.insee.queen.domain.synchronisation.service.SynchronisationService;
import lombok.Getter;

public class SynchronisationFakeService implements SynchronisationService {

    @Getter
    private String synchronisedInterrogationId = null;

    @Override
    public void synchronise(String interrogationId) {
        this.synchronisedInterrogationId = interrogationId;
    }

    public void reset() {
        this.synchronisedInterrogationId = null;
    }
}
