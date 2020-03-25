package fr.insee.queen.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
* Entity Data : represent the entity table in DB
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
public class Data extends AbstractEntity {
	/**
	* The id of data 
	*/
	@Id
	@GeneratedValue
	private Long id;
	
	/**
	* The version of data ('INIT' or 'COLLECTED')
	*/
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(9) default 'INIT'")
	private Version version;
	
	/**
	* The value of data (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private String value;
	
	/**
	* The ReportingUnit associated to the comment
	*/
	@OneToOne
	private ReportingUnit reportingUnit;
	
	/**
	 * @return id of comment
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
	 * @return version of comment
	 */
	public Version getVersion() {
		return version;
	}
	/**
	 * @param version version to set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}
	/**
	 * @return value of comment
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return ReportingUnit associated to the comment
	 */
	public ReportingUnit getReportingUnit() {
		return reportingUnit;
	}
	/**
	 * @param reportingUnit reportingUnit to set
	 */
	public void setReportingUnit(ReportingUnit reportingUnit) {
		this.reportingUnit = reportingUnit;
	}
	
}
