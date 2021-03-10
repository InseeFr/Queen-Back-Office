package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.json.simple.JSONObject;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
* Entity Data : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
public class Data extends AbstractEntity {
	/**
	* The id of data 
	*/
	@Id
	@GeneratedValue
	private Long id;
	
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
	private JSONObject value;
	
	/**
	* The SurveyUnit associated to the comment
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;
	
	/**
	 * @return id of comment
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id id to set
	 */
	public void setId(Long id) {
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
	public JSONObject getValue() {
		return value;
	}
	/**
	 * @param value value to set
	 */
	public void setValue(JSONObject value) {
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
