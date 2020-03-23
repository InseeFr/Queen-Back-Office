package fr.insee.queen.queen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.domain.Operation;
import fr.insee.queen.queen.dto.operation.OperationDto;
import fr.insee.queen.queen.repository.OperationRepository;

/**
* OperationController is the Controller using to manage {@link Operation} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping
public class OperationController {
	/**
	* The operation repository using to access to table 'operation' in DB 
	*/
	@Autowired
	private OperationRepository operationRepository;
	
	/**
	* This method is using to get all operations
	* 
	* @return List of all {@link OperationDto}
	*/
	@GetMapping(path = "/operations")
	public List<OperationDto> listOperation(){
		return operationRepository.findDtoBy();
	}
	
}
