package fr.insee.queen.queen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.dto.operation.OperationDto;
import fr.insee.queen.queen.repository.OperationRepository;

@RestController
@RequestMapping
public class OperationController {
	@Autowired
	private OperationRepository operationRepository;
	
	@GetMapping(path = "/operations")
	public List<OperationDto> listOperation(){
		return operationRepository.findDtoBy();
	}
	
}
