package fr.insee.queen.queen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.Operation;
import fr.insee.queen.queen.dto.operation.OperationDto;

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
