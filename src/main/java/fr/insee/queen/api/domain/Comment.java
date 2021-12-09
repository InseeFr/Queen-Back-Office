package fr.insee.queen.api.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
* Entity Comment : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="comment")
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
public class Comment {
	/**
	* The id of comment 
	*/
	@Id
	@org.springframework.data.annotation.Id
    protected UUID id;
	
	/**
	* The value of comment (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JsonNode value;

	/**
	* The SurveyUnit associated to the comment
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;
	
	public Comment() {
		super();
		this.id = UUID.randomUUID();
	}
	public Comment(UUID id, JsonNode value, SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.value = value;
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
	 * @return value of comment
	 */
	public JsonNode getValue() {
		return value;
	}
	/**
	 * @param value value to set
	 */
	public void setValue(JsonNode value) {
		this.value = value;
	}
	/**
	 * @return SurveyUnit associated to the comment
	 */
	public SurveyUnit getSurveyUnit() {
		return surveyUnit;
	}
	/**
	 * @param surveyUnit surveyUnit to set
	 */
	public void setSurveyUnit(SurveyUnit surveyUnit) {
		this.surveyUnit = surveyUnit;
	}
	
}
