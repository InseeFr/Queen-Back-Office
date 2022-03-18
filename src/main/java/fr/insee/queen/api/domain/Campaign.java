package fr.insee.queen.api.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
* Entity Campaign : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="campaign")
public class Campaign {
	/**
	* The id of campaign 
	*/
	@Id
	@org.springframework.data.annotation.Id
	@Column(length=50)
	private String id;
  
	/**
	* The label of campaign 
	*/
	@Column(length=255, nullable = false)
	private String label;
	

	@OneToOne( mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true )
	private Metadata metadata;
  

	@OneToMany(fetch = FetchType.LAZY, targetEntity=QuestionnaireModel.class, cascade = CascadeType.ALL, mappedBy="campaign" )
	private Set<QuestionnaireModel> questionnaireModels = new HashSet<>();
	 
	public Campaign() {
		super();
	}
	public Campaign(String id, String label, Set<QuestionnaireModel> questionnaireModels) {
		super();
		this.id = id;
		this.label = label;
		this.questionnaireModels = questionnaireModels;
	}
	
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
	
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Set<QuestionnaireModel> getQuestionnaireModels(){
		return questionnaireModels;
	}
	public void setQuestionnaireModels(Set<QuestionnaireModel> questionnaireModels){
		this.questionnaireModels = questionnaireModels;
	}
	
}
