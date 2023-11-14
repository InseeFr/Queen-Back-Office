package fr.insee.queen.api.campaign.repository.entity;

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
 * Nomenclature entity
 */
@Entity
@Table(name = "nomenclature")
@Getter
@Setter
@AllArgsConstructor
public class NomenclatureDB {
    /**
     * nomenclature id
     */
    @Id
    @org.springframework.data.annotation.Id
    @Column(length = 50)
    private String id;
    /**
     * nomenclature label
     */
    @Column(nullable = false)
    private String label;

    /**
     * nomenclature value (json)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String value;

    public NomenclatureDB() {
        super();
    }
}
