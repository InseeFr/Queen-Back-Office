package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.json.simple.JSONObject;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
/**
* Entity Nomenclature : represent the entity table in DB
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
public class Nomenclature {
	/**
	* The id of nomenclature 
	*/
	@Id
	@Column(length=50)
	private String id;
	/**
	* The label of nomenclature 
	*/
	@Column(length=255, nullable = false)
	private String label;
	
	/**
	* The value of nomenclature (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JSONObject value;

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
	 * @return value of nomenclature
	 */
	public JSONObject getValue() {
		return value;
	}
	/**
	 * @param value value to set
	 */
	public void setValue(JSONObject value) {
		this.value = value;
	}

}
