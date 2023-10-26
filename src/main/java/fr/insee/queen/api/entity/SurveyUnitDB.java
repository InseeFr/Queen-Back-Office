package fr.insee.queen.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
* Entity SurveyUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="survey_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUnitDB {
	/**
	* The id of surveyUnit 
	*/
	@Id
	@org.springframework.data.annotation.Id
	private String id;
	
	/**
	* The campaign associated to the reporting unit
	*/
	@ManyToOne
    private CampaignDB campaign ;
	
	/**
	* The questionnaire model associated to the reporting unit
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "questionnaire_model_id", referencedColumnName = "id")
	private QuestionnaireModelDB questionnaireModel;

	@Embedded
	private StateDataDB stateData;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="personalization", columnDefinition = "jsonb")
	private String personalization;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="data", columnDefinition = "jsonb")
	private String data;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="comment", columnDefinition = "jsonb")
	private String comment;

	public SurveyUnitDB(String id, CampaignDB campaign, QuestionnaireModelDB questionnaireModel) {
		this.id = id;
		this.campaign = campaign;
		this.questionnaireModel = questionnaireModel;
	}
}
