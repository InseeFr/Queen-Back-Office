package fr.insee.queen.queen.domain;

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

@Entity
@Table
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
public class Data extends AbstractEntity {
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(9) default 'INIT'")
	private Version version;
	
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private String value;
	
	@OneToOne
	private ReportingUnit reportingUnit;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Version getVersion() {
		return version;
	}
	public void setVersion(Version version) {
		this.version = version;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ReportingUnit getReportingUnit() {
		return reportingUnit;
	}
	public void setReportingUnit(ReportingUnit reportingUnit) {
		this.reportingUnit = reportingUnit;
	}
	
}
