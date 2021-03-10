package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

@Entity
@Table
public class Metadata {
	
	/**
	 * The id of the Metadata
	 */
	@Id
	@GeneratedValue
	private int idMetadata;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private JSONObject value;
	
	/**
	 * The campaign associated to the Metadata
	 */
	@OneToOne
	private Campaign campaign;

	/**
	 * @return the idMetadata
	 */
	public int getIdMetadata() {
		return idMetadata;
	}

	/**
	 * @param idMetadata the idMetadata to set
	 */
	public void setIdMetadata(int idMetadata) {
		this.idMetadata = idMetadata;
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
