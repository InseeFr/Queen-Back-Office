package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

@Entity
@Table
public class Personnalization extends AbstractEntity{
	
	/**
	* The id of personnalization 
	*/
	@Id
	private int idPersonnalization;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JSONObject value;
	
	/**
	* The SurveyUnit associated to the personnalization
	*/
	@OneToOne
	private SurveyUnit surveyUnit;

	/**
	 * @return the idPersonnalization
	 */
	public int getIdPersonnalization() {
		return idPersonnalization;
	}

	/**
	 * @param idPersonnalization the idPersonnalization to set
	 */
	public void setIdPersonnalization(int idPersonnalization) {
		this.idPersonnalization = idPersonnalization;
	}

	/**
	 * @return the value
	 */
	public JSONObject getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JSONObject value) {
		this.value = value;
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
