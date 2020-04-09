package fr.insee.queen.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
* Entity ReportingUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class ReportingUnit extends AbstractEntity {
	/**
	* The id of reportingUnit 
	*/
	@Id
	@GeneratedValue
	private Long id;
	
	/**
	* The operation associated to the reporting unit
	*/
	@ManyToOne
    private Operation operation ;
	/**
	 * @return id of reportingUnit
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return operation of reportingUnit
	 */
	public Operation getOperation() {
		return operation;
	}
	/**
	 * @param operation operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
}
