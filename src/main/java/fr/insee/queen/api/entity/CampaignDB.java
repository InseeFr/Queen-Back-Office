package fr.insee.queen.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@AllArgsConstructor
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


	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="metadata", columnDefinition = "jsonb")
	private String metadata;
  

	@OneToMany(fetch = FetchType.LAZY, targetEntity= QuestionnaireModelDB.class, cascade = CascadeType.ALL, mappedBy="campaign" )
	private Set<QuestionnaireModelDB> questionnaireModels = new HashSet<>();
}
