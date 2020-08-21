package fr.insee.queen.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
* Entity SurveyUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class SurveyUnit extends AbstractEntity {
	/**
	* The id of surveyUnit 
	*/
	@Id
	private String id;
	
	/**
	* The campaign associated to the reporting unit
	*/
	@ManyToOne
    private Campaign campaign ;
	/**
	 * @return id of surveyUnit
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
	 * @return campaign of surveyUnit
	 */
	public Campaign getCampaign() {
		return campaign;
	}
	/**
	 * @param campaign campaign to set
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
}
