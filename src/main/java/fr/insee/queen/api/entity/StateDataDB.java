package fr.insee.queen.api.entity;

import fr.insee.queen.api.dto.statedata.StateDataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class StateDataDB {
	
	/**
	 * The State of the state data
	 */
	@Enumerated(EnumType.STRING)
	@Column(length=8)
	private StateDataType state;
	
	/**
	* The save date of State 
	*/
	@Column(name = "state_date")
	private Long date;
	
	/**
	 * The current page of the StateData
	 */
	@Column(name = "state_current_page")
	private String currentPage;
}
