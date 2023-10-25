package fr.insee.queen.api.entity;

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
public class CampaignDB {
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
	private MetadataDB metadata;
  

	@OneToMany(fetch = FetchType.LAZY, targetEntity= QuestionnaireModelDB.class, cascade = CascadeType.ALL, mappedBy="campaign" )
	private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();

	public CampaignDB(String id, String label, Set<QuestionnaireModelDB> questionnaireModels) {
		super();
		this.id = id;
		this.label = label;
		this.questionnaireModels = questionnaireModels;
	}
}
