package fr.insee.queen.jms.config;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SynchronisationFakeRepository implements SynchronisationRepository {

    @Override
    public Interrogation synchronise(String interrogationId) {
        // Return null for tests - synchronisation is not used in JMS integration tests
        return null;
    }
}
