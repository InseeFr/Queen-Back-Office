package fr.insee.queen.queen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.dto.operation.QuestionnaireModelDto;
import fr.insee.queen.queen.repository.QuestionnaireModelRepository;

@RestController
@RequestMapping
public class QuestionnaireModelController {
	@Autowired
	private QuestionnaireModelRepository questionnaireModelRepository;
	
	@GetMapping(path = "/operation/{id}/questionnaire")
	public QuestionnaireModelDto getQuestionnaireModelByOperationId(@PathVariable(value = "id") String id){
		return questionnaireModelRepository.findDtoByOperationId(id);
	}
	
	
}
