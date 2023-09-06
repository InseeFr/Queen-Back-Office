package fr.insee.queen.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
* Entity surveyUnitTempZone
* 
* @author Laurent Caouissin
* 
*/
@Entity
@Table(name="survey_unit_temp_zone")
@Getter
@Setter
public class SurveyUnitTempZone {

	/**
	* The unique id of surveyUnitTempZone
	*/
	@Id
	@org.springframework.data.annotation.Id
    private UUID id;

	/**
	 * The id of surveyUnit
	 */
	@Column(name = "survey_unit_id")
	private String surveyUnitId;

	/**
	 * The id of user
	 */
	@Column(name="user_id")
	private String userId;

	/**
	 * The date of save
	 */
	@Column
	private Long date;
	/**
	* The value of surveyUnit (jsonb format)
	*/
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String surveyUnit;

	public SurveyUnitTempZone(){
		super();
		this.id = UUID.randomUUID();
	}

	public SurveyUnitTempZone(String surveyUnitId, String userId, Long date, String surveyUnit) {
		super();
		this.id = UUID.randomUUID();
		this.surveyUnitId = surveyUnitId;
		this.userId = userId;
		this.date = date;
		this.surveyUnit = surveyUnit;
	}
}
