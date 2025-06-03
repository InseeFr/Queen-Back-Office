package fr.insee.queen.infrastructure.db.interrogation.repository;

import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.StateDataJpaRepository;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.InterrogationJpaRepository;
import fr.insee.queen.infrastructure.db.interrogation.entity.StateDataDB;
import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO to handle state data
 */
@Repository
@AllArgsConstructor
public class StateDataDao implements StateDataRepository {

    private final StateDataJpaRepository jpaRepository;
    private final InterrogationJpaRepository interrogationJpaRepository;

    @Override
    public Optional<StateData> find(String interrogationId) {
        return jpaRepository.findByInterrogationId(interrogationId);
    }

    @Override
    public void save(String interrogationId, StateData stateData) {
        if (stateData == null) {
            return;
        }

        int countUpdated = jpaRepository.updateStateData(interrogationId, stateData.date(), stateData.currentPage(), stateData.state());
        if (countUpdated == 0) {
            create(interrogationId, stateData);
        }
    }

    @Override
    public void create(String interrogationId, StateData stateData) {
        InterrogationDB interrogation = interrogationJpaRepository.getReferenceById(interrogationId);
        StateDataDB stateDataDB = new StateDataDB(stateData.state(), stateData.date(), stateData.currentPage(), interrogation);
        jpaRepository.save(stateDataDB);
    }

    @Override
    public boolean exists(String interrogationId) {
        return jpaRepository.existsByInterrogationId(interrogationId);
    }

    /**
     * Delete state data for an interrogation
     * @param interrogationId interrogation id
     */
    public void deleteByInterrogationId(String interrogationId) {
        jpaRepository.deleteByInterrogationId(interrogationId);
    }

    /**
     * Delete all interrogations state data for a campaign
     * @param campaignId campaign id
     */
    public void deleteStateDatas(String campaignId) {
        jpaRepository.deleteStateDatas(campaignId);
    }
}
