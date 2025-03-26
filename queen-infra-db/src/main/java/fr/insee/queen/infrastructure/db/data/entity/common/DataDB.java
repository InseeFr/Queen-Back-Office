package fr.insee.queen.infrastructure.db.data.entity.common;

import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "data")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@DiscriminatorColumn(name="encrypted", discriminatorType = DiscriminatorType.INTEGER)
public abstract class DataDB {

    /**
     * The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The Interrogation associated
     */
    @OneToOne
    @JoinColumn(name = "interrogation_id", referencedColumnName = "id")
    private InterrogationDB interrogation;
}