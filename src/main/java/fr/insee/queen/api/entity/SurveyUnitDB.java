package fr.insee.queen.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@OneToOne(mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private StateDataDB stateData;

	@OneToOne(mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private PersonalizationDB personalization;

	@OneToOne(mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private DataDB data;

	@OneToOne(mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private CommentDB comment;

	public SurveyUnitDB(String id, CampaignDB campaign, QuestionnaireModelDB questionnaireModel) {
		this.id = id;
		this.campaign = campaign;
		this.questionnaireModel = questionnaireModel;
	}
}
