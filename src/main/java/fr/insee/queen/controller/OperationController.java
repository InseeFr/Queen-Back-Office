package fr.insee.queen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.dto.operation.OperationDto;
import fr.insee.queen.repository.OperationRepository;

@RestController
@RequestMapping(path = "/api/operation")
public class OperationController {
	@Autowired
	private OperationRepository operationRepository;
	
	@GetMapping
	public List<OperationDto> listOperation(){
		return operationRepository.findDtoBy();
	}
	
	
}
