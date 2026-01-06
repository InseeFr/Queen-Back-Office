package fr.insee.queen.infrastructure.db.interrogation.entity;

import fr.insee.queen.domain.interrogation.model.StateDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "state_data")
@Getter
@Setter
@NoArgsConstructor
public class StateDataDB {

    /**
     * The id of the state data
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The State of the state data
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private StateDataType state;

    /**
     * The save date of State
     */
    @Column
    private Long date;

    /**
     * The current page of the StateData
     */
    @Column(name = "current_page")
    private String currentPage;

    /**
     * The Interrogation linked to the StateData
     */
    @OneToOne
    @JoinColumn(name = "interrogation_id", referencedColumnName = "id")
    private fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB interrogation;

    /**
     * The leaf states linked to this StateData
     */
    @OneToMany(mappedBy = "stateData", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeafStateDB> leafStates = new ArrayList<>();

    public StateDataDB(StateDataType state, Long date, String currentPage, fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB interrogation) {
        this.state = state;
        this.date = date;
        this.currentPage = currentPage;
        this.interrogation = interrogation;
    }

    public void setLeafStates(List<LeafStateDB> leafStates) {
        this.leafStates.clear();
        if (leafStates != null) {
            this.leafStates.addAll(leafStates);
        }
    }
}
