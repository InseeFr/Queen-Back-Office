package fr.insee.queen.queen.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
/**
* Entity QuestionnaireModel : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
public class QuestionnaireModel {
	/**
	* The id of questionnaire 
	*/
	@Id
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
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private String model;
	
	/**
	* The list of required of required nomenclature 
	*/
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "required_nomenclature", joinColumns = { @JoinColumn(name = "id_required_nomenclature") }, inverseJoinColumns = { @JoinColumn(name = "code") })
	private Set<Nomenclature> nomenclatures;
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
	 * @return model of nomenclature
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

}
