package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.model.Interrogation;

import java.util.List;

public interface InterrogationBatchService {
    void saveInterrogations(List<Interrogation> interrogations);
    void saveInterrogation(Interrogation interrogation);
    void delete(List<String> interrogationIds);
}
