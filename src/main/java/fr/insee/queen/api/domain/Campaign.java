package fr.insee.queen.api.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
* Entity Campaign : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class Campaign {
	/**
	* The id of campaign 
	*/
	@Id
	@Column(length=50)
	private String id;
  
	/**
	* The label of campaign 
	*/
	@Column(length=255, nullable = false)
	private String label;
  
	@OneToMany(targetEntity=QuestionnaireModel.class, cascade = CascadeType.ALL, mappedBy="campaign" )
	private Set<QuestionnaireModel> questionnaireModels = new HashSet<>();
	 
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
	
	public Set<QuestionnaireModel> getQuestionnaireModels(){
		return questionnaireModels;
	}
	public void getQuestionnaireModels(Set<QuestionnaireModel> questionnaireModels){
		this.questionnaireModels = questionnaireModels;
	}
	
}
