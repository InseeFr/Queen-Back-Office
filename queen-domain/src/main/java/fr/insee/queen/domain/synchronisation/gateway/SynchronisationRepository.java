package fr.insee.queen.domain.synchronisation.gateway;

import fr.insee.queen.domain.interrogation.model.Interrogation;

public interface SynchronisationRepository {
    Interrogation synchronise(String interrogationId);
}
