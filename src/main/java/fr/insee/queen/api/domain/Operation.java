package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
* Entity Operation : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class Operation {
	/**
	* The id of operation 
	*/
	@Id
	@Column(length=50)
	private String id;
	/**
	* The label of operation 
	*/
	@Column(length=255, nullable = false)
	private String label;
	 
	/**
	* The QuestionnaireModel associated to operation
	*/
	@ManyToOne
	private QuestionnaireModel questionnaireModel;
	/**
	 * @return id of nomenclature
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
	 * @return label of nomenclature
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return QuestionnaireModel associated to operation
	 */
	public QuestionnaireModel getQuestionnaireModel() {
		return questionnaireModel;
	}
	/**
	 * @param questionnaireModel questionnaireModel to set
	 */
	public void setQuestionnaireModel(QuestionnaireModel questionnaireModel) {
		this.questionnaireModel = questionnaireModel;
	}
}
