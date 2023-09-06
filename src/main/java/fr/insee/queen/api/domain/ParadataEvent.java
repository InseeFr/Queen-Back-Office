package fr.insee.queen.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="paradata_event")
@Getter
@Setter
@AllArgsConstructor
public class ParadataEvent {
	
	/**
	 * The id of the ParadataEvent
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
	
	public ParadataEvent() {
		super();
		this.id = UUID.randomUUID();
	}
}
