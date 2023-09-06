package fr.insee.queen.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="metadata")
@Getter
@Setter
@AllArgsConstructor
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
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String value;
	
	/**
	 * The campaign associated to the Metadata
	 */
	@OneToOne
	private Campaign campaign;

	public Metadata() {
		super();
		this.id = UUID.randomUUID();
	}
}
