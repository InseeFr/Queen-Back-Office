package fr.insee.queen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.domain.Operation;
import fr.insee.queen.api.dto.operation.OperationDto;

/**
* OperationRepository is the repository using to access to Operation table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface OperationRepository extends JpaRepository<Operation, String> {
	/**
	* This method retrieve all Operation in DB
	* 
	* @return List of all {@link OperationDto}
	*/
	List<OperationDto> findDtoBy();
}
