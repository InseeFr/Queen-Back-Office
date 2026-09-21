package fr.insee.queen.domain.synchronisation.service;

import fr.insee.queen.domain.interrogation.model.Interrogation;

public interface SynchronisationService {
    Interrogation synchronise(String interrogationId);
}