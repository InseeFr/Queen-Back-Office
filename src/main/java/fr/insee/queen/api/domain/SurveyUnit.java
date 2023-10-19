package fr.insee.queen.api.domain;

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
public class SurveyUnit {
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
    private Campaign campaign ;
	
	/**
	* The questionnaire model associated to the reporting unit
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "questionnaire_model_id", referencedColumnName = "id")
	private QuestionnaireModel questionnaireModel;

	@Embedded
	private StateData stateData;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="personalization", columnDefinition = "jsonb")
	private String personalization;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="data", columnDefinition = "jsonb")
	private String data;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="comment", columnDefinition = "jsonb")
	private String comment;

	public SurveyUnit(String id, Campaign campaign, QuestionnaireModel questionnaireModel) {
		this.id = id;
		this.campaign = campaign;
		this.questionnaireModel = questionnaireModel;
	}
}
