package fr.insee.queen.api.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "state_data")
public class StateData {

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
	private SurveyUnit surveyUnit;

	public StateData() {
		super();
		this.id = UUID.randomUUID();
	}

	public StateData(UUID id, StateDataType state, Long date, String currentPage, SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.state = state;
		this.date = date;
		this.currentPage = currentPage;
		this.surveyUnit = surveyUnit;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public StateDataType getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(StateDataType state) {
		this.state = state;
	}

	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}

	/**
	 * @return the currentPage
	 */
	public String getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	
	/**
	 * @return the surveyUnit
	 */
	public SurveyUnit getSurveyUnit() {
		return surveyUnit;
	}

	/**
	 * @param surveyUnit the surveyUnit to set
	 */
	public void setSurveyUnit(SurveyUnit surveyUnit) {
		this.surveyUnit = surveyUnit;
	}
	
}
