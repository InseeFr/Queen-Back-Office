package fr.insee.queen.api.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.domain.ParadataEvent;
import org.springframework.stereotype.Repository;

/**
* ParadataEventRepository is the repository using to access to ParadataEvent table in DB
* 
* @author Corcaud Samuel
* 
*/
@Repository
public interface ParadataEventRepository extends JpaRepository<ParadataEvent, UUID> {


}
