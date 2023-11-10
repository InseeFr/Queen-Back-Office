package fr.insee.queen.api.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
* Entity Nomenclature : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table(name="nomenclature")
@Getter
@Setter
@AllArgsConstructor
public class NomenclatureDB {
	/**
	* The id of nomenclature 
	*/
	@Id
	@org.springframework.data.annotation.Id
	@Column(length=50)
	private String id;
	/**
	* The label of nomenclature 
	*/
	@Column(nullable = false)
	private String label;
	
	/**
	* The value of nomenclature (jsonb format)
	*/
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String value;

	public NomenclatureDB() {
		super();
	}
}
