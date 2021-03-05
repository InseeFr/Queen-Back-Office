package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

@Entity
@Table
public class ParadataEvent {
	
	/**
	 * The id of the ParadataEvent
	 */
	@Id
	@GeneratedValue
	private int idParadataEvent;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JSONObject value;
	
	public ParadataEvent() {
		super();
	}

	/**
	 * @return the idParadataEvent
	 */
	public int getIdParadataEvent() {
		return idParadataEvent;
	}

	/**
	 * @param idParadataEvent the idParadataEvent to set
	 */
	public void setIdParadataEvent(int idParadataEvent) {
		this.idParadataEvent = idParadataEvent;
	}

	/**
	 * @return the value
	 */
	public JSONObject getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JSONObject value) {
		this.value = value;
	}
}
