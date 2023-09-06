package fr.insee.queen.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
* Entity Data : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="data")
@Getter
@Setter
@AllArgsConstructor
public class Data {
	
	
	/**
	* The id of data 
	*/
	@Id
	@org.springframework.data.annotation.Id
    protected UUID id;
	
	/**
	* The value of data (jsonb format)
	*/
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String value;
	
	/**
	* The SurveyUnit associated to the comment
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;
	
	public Data() {
		super();
		this.id = UUID.randomUUID();
	}
}
