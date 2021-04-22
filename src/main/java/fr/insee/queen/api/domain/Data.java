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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
* Entity Data : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="data")
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
@Document(collection="data")
public class Data {
	
	
	/**
	* The id of data 
	*/
	@Id
	@org.springframework.data.annotation.Id
    protected UUID id;
	
	/**
	* The version of data ('INIT' or 'COLLECTED')
	*/
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(9) default 'INIT'")
	private Version version;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JsonNode value;
	
	/**
	* The SurveyUnit associated to the comment
	*/
	@DBRef
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;
	
	public Data() {
		super();
		this.id = UUID.randomUUID();
	}
	public Data(UUID id, Version version, JsonNode value, SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.version = version;
		this.value = value;
		this.surveyUnit = surveyUnit;
	}
	/**
	 * @return id of comment
	 */
	public UUID getId() {
		return id;
	}
	/**
	 * @param id id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}
	/**
	 * @return version of comment
	 */
	public Version getVersion() {
		return version;
	}
	/**
	 * @param version version to set
	 */
	public void setVersion(Version version) {
		this.version = version;
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
