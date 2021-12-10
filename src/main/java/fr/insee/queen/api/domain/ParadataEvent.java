package fr.insee.queen.api.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="paradata_event")
public class ParadataEvent {
	
	/**
	 * The id of the ParadataEvent
	 */
	@Id
	@org.springframework.data.annotation.Id
	@Column(name = "id")
	private UUID id;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JsonNode value;
	
	public ParadataEvent() {
		super();
		this.id = UUID.randomUUID();
	}

	public ParadataEvent(UUID id, JsonNode value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * @return the idParadataEvent
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param idParadataEvent the idParadataEvent to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public JsonNode getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JsonNode value) {
		this.value = value;
	}
}
