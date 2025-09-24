package fr.insee.queen.domain.interrogation.gateway;

import fr.insee.queen.domain.interrogation.model.Interrogation;

import java.util.List;

public interface InterrogationBatchRepository {
    void upsertAll(List<Interrogation> interrogations);
    void deleteAll(List<String> interrogationIds);
}
