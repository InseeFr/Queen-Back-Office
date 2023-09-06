package fr.insee.queen.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="personalization")
@Getter
@Setter
@AllArgsConstructor
public class Personalization {
	
	/**
	* The id of personalization 
	*/
	@Id
	@org.springframework.data.annotation.Id
	@Column(name = "id")
	private UUID id;
	
	/**
	* The value of data (jsonb format)
	*/
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String value;
	
	
	/**
	* The SurveyUnit associated to the personalization
	*/
	@OneToOne
	@JoinColumn(name = "survey_unit_id", referencedColumnName = "id")
	private SurveyUnit surveyUnit;

	public Personalization() {
		super();
		this.id = UUID.randomUUID();
	}
}
