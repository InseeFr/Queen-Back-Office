package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.model.Interrogation;

import java.util.List;

public interface InterrogationBatchService {
    void saveInterrogations(List<Interrogation> interrogations);
    void delete(List<String> interrogationIds);
}
