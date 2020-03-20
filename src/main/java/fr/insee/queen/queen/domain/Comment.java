package fr.insee.queen.queen.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
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
public class Comment extends AbstractEntity {
	@Id
	@GeneratedValue
	private Long id;
	
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
