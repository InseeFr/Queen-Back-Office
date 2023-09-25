package fr.insee.queen.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
* Entity Campaign : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="campaign")
@Getter
@Setter
@NoArgsConstructor
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

	public Campaign(String id, String label, Set<QuestionnaireModel> questionnaireModels) {
		super();
		this.id = id;
		this.label = label;
		this.questionnaireModels = questionnaireModels;
	}
}
