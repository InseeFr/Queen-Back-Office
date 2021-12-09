package fr.insee.queen.api.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="personalization")
public class Personalization {
	
	/**
	* The id of personalization 
	*/
	@Id
	@org.springframework.data.annotation.Id
	@Column(name = "id")
	private UUID id;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JsonNode value;
	
	
	/**
	* The SurveyUnit associated to the personalization
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;

	public Personalization() {
		super();
		this.id = UUID.randomUUID();
	}

	public Personalization(UUID id, JsonNode value, SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.value = value;
		this.surveyUnit = surveyUnit;
	}

	/**
	 * @return the idPersonalization
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param idPersonalization the idPersonalization to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public JsonNode getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JsonNode value) {
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
