package fr.insee.queen.infrastructure.db.interrogation.repository;

import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.LeafState;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.infrastructure.db.interrogation.entity.LeafStateDB;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.StateDataJpaRepository;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.InterrogationJpaRepository;
import fr.insee.queen.infrastructure.db.interrogation.entity.StateDataDB;
import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        return jpaRepository.findEntityByInterrogationId(interrogationId)
                .map(this::toStateData);
    }

    @Override
    public void save(String interrogationId, StateData stateData) {
        if (stateData == null) {
            return;
        }

        Optional<StateDataDB> existingOpt = jpaRepository.findEntityByInterrogationId(interrogationId);
        if (existingOpt.isPresent()) {
            StateDataDB existing = existingOpt.get();
            existing.setState(stateData.state());
            existing.setDate(stateData.date());
            existing.setCurrentPage(stateData.currentPage());
            updateLeafStates(existing, stateData.leafStates());
            jpaRepository.save(existing);
        } else {
            create(interrogationId, stateData);
        }
    }

    @Override
    public void create(String interrogationId, StateData stateData) {
        InterrogationDB interrogation = interrogationJpaRepository.getReferenceById(interrogationId);
        StateDataDB stateDataDB = new StateDataDB(stateData.state(), stateData.date(), stateData.currentPage(), interrogation);
        updateLeafStates(stateDataDB, stateData.leafStates());
        jpaRepository.save(stateDataDB);
    }

    private void updateLeafStates(StateDataDB stateDataDB, List<LeafState> leafStates) {
        stateDataDB.getLeafStates().clear();
        if (leafStates != null && !leafStates.isEmpty()) {
            List<LeafStateDB> leafStateDBs = leafStates.stream()
                    .map(ls -> new LeafStateDB(ls.state(), ls.date(), stateDataDB))
                    .toList();
            stateDataDB.getLeafStates().addAll(leafStateDBs);
        }
    }

    private StateData toStateData(StateDataDB db) {
        List<LeafState> leafStates = db.getLeafStates().stream()
                .map(ls -> new LeafState(ls.getState(), ls.getDate()))
                .toList();
        return new StateData(db.getState(), db.getDate(), db.getCurrentPage(), leafStates);
    }

    @Override
    public boolean exists(String interrogationId) {
        return jpaRepository.existsByInterrogationId(interrogationId);
    }
}
