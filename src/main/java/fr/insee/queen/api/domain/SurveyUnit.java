package fr.insee.queen.api.domain;

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

	@OneToOne( mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private Comment comment;

	@OneToOne( mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private Data data;

	@OneToOne(mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private StateData stateData;

	@OneToOne( mappedBy = "surveyUnit", cascade = CascadeType.ALL )
	private Personalization personalization;
}
