package fr.insee.queen.infrastructure.db.interrogation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "leaf_state")
@Getter
@Setter
@NoArgsConstructor
public class LeafStateDB {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String state;

    @Column(nullable = false)
    private Long date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_data_id", nullable = false)
    private StateDataDB stateData;

    public LeafStateDB(String state, Long date, StateDataDB stateData) {
        this.state = state;
        this.date = date;
        this.stateData = stateData;
    }
}
