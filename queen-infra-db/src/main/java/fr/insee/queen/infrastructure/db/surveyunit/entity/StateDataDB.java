package fr.insee.queen.infrastructure.db.surveyunit.entity;

import fr.insee.queen.domain.surveyunit.model.StateDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * The SurveyUnit linked to the StateData
     */
    @OneToOne
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
    private SurveyUnitDB surveyUnit;

    public StateDataDB(StateDataType state, Long date, String currentPage, SurveyUnitDB surveyUnit) {
        this.state = state;
        this.date = date;
        this.currentPage = currentPage;
        this.surveyUnit = surveyUnit;
    }
}
