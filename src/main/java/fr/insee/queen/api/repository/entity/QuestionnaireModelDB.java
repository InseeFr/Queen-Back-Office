package fr.insee.queen.api.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

/**
* Entity QuestionnaireModel : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="questionnaire_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireModelDB {
	/**
	* The id of questionnaire 
	*/
	@Id
	@org.springframework.data.annotation.Id
	@Column(length=50)
	private String id;
	
	/**
	* The label of questionnaire 
	*/
	@Column(length=255, nullable = false)
	private String label;
	
	/**
	* The model of questionnaire (jsonb format) 
	*/

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String value;
	
	/**
	* The list of required of required nomenclature 
	*/
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch=FetchType.LAZY)
	@JoinTable(name = "required_nomenclature", 
	joinColumns = { @JoinColumn(name = "id_required_nomenclature") }, inverseJoinColumns = { @JoinColumn(name = "code") })
	private Set<NomenclatureDB> nomenclatures = new HashSet<>();
	
	/**
	 * The campaign associated to the questionnaireModel
	 */
	@ManyToOne
	private CampaignDB campaign;

	public QuestionnaireModelDB(String id, String label, String value, Set<NomenclatureDB> nomenclatures) {
		this.id = id;
		this.label = label;
		this.value = value;
		this.nomenclatures = nomenclatures;
	}
}
