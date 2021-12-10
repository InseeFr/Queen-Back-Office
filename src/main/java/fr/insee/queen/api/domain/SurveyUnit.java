package fr.insee.queen.api.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
* Entity SurveyUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="survey_unit")
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
	
	public SurveyUnit(String id, Campaign campaign, QuestionnaireModel questionnaireModel, Comment comment, Data data,
			StateData stateData, Personalization personalization) {
		super();
		this.id = id;
		this.campaign = campaign;
		this.questionnaireModel = questionnaireModel;
		this.comment = comment;
		this.data = data;
		this.stateData = stateData;
		this.personalization = personalization;
	}
	
	public SurveyUnit() {
		super();
	}

	public Personalization getPersonalization() {
		return personalization;
	}
	public void setPersonalization(Personalization personalization) {
		this.personalization = personalization;
	}
	/**
	 * @return id of surveyUnit
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return campaign of surveyUnit
	 */
	public Campaign getCampaign() {
		return campaign;
	}
	/**
	 * @param campaign campaign to set
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	/**
	 * @return the questionnaireModel
	 */
	public QuestionnaireModel getQuestionnaireModel() {
		return questionnaireModel;
	}
	
	/**
	 * @return the questionnaireModelId
	 */
	public String getQuestionnaireModelId() {
		return questionnaireModel== null ? null : questionnaireModel.getId();
	}
	/**
	 * @param questionnaireModel the questionnaireModel to set
	 */
	public void setQuestionnaireModel(QuestionnaireModel questionnaireModel) {
		this.questionnaireModel = questionnaireModel;
	}
	
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public StateData getStateData() {
		return stateData;
	}
	public void setStateData(StateData stateData) {
		this.stateData = stateData;
	}
	
}
