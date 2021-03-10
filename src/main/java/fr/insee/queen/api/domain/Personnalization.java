package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Entity
@Table
public class Personnalization extends AbstractEntity{
	
	/**
	* The id of personnalization 
	*/
	@Id
	@GeneratedValue
	private int idPersonnalization;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JSONArray value;
	
	
	/**
	* The SurveyUnit associated to the personnalization
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
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
	public JSONArray getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JSONArray value) {
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
