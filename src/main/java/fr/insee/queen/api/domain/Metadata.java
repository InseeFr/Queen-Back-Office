package fr.insee.queen.api.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="metadata")
public class Metadata {
	
	/**
	 * The id of the Metadata
	 */
	@Id
	@org.springframework.data.annotation.Id
	private UUID id;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JsonNode value;
	
	/**
	 * The campaign associated to the Metadata
	 */
	@OneToOne
	private Campaign campaign;

	public Metadata() {
		super();
		this.id = UUID.randomUUID();
	}

	public Metadata(UUID id, JsonNode value, Campaign campaign) {
		super();
		this.id = id;
		this.value = value;
		this.campaign = campaign;
	}

	/**
	 * @return the idMetadata
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param idMetadata the idMetadata to set
	 */
	public void setIdMetadata(UUID id) {
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

	/**
	 * @return the campaign
	 */
	public Campaign getCampaign() {
		return campaign;
	}

	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

}
