package fr.insee.queen.api.repository.entity;

import fr.insee.queen.api.dto.statedata.StateDataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "state_data")
@Getter
@Setter
@AllArgsConstructor
public class StateDataDB {

	/**
	 * The id of the state data
	 */
	@Id
	@org.springframework.data.annotation.Id
	@Column(name = "id")
	protected UUID id;

	/**
	 * The State of the state data
	 */
	@Enumerated(EnumType.STRING)
	@Column(length=8)
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
	 * The SurveyUnit associated to the StateData
	 */
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnitDB surveyUnit;

	public StateDataDB() {
		super();
		this.id = UUID.randomUUID();
	}
}
