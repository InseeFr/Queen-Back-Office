package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class StateData extends AbstractEntity{

	/**
	 * The id of the state data
	 */
	@Id
	@GeneratedValue
	private int idStateData;
	
	/**
	 * The State of the state data
	 */
	@Enumerated(EnumType.STRING)
	@Column(length=8)
	private StateStateData state;
	
	/**
	* The save date of State 
	*/
	@Column
	private Long date;
	
	/**
	 * The current page of the StateData
	 */
	@Column
	private int currentPage;
	
	/**
	* The SurveyUnit associated to the StateData
	*/
	@OneToOne
	private SurveyUnit surveyUnit;

	/**
	 * @return the idStateData
	 */
	public int getIdStateData() {
		return idStateData;
	}

	/**
	 * @param idStateData the idStateData to set
	 */
	public void setIdStateData(int idStateData) {
		this.idStateData = idStateData;
	}

	/**
	 * @return the state
	 */
	public StateStateData getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(StateStateData state) {
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
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
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
