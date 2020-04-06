package fr.insee.queen.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import io.swagger.annotations.ApiOperation;

/**
* QuestionnaireModelController is the Controller using to manage {@link QuestionnaireModel} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class QuestionnaireModelController {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireModelController.class);

	/**
	* The questionnaire model repository using to access to table 'questionnaire_model' in DB 
	*/
	@Autowired
	private QuestionnaireModelRepository questionnaireModelRepository;
	
	/**
	* This method is using to get the questionnaireModel associated to a specific operation 
	* 
	* @param id the id of operation
	* @return the {@link QuestionnaireModelDto} associated to the operation
	*/
	@ApiOperation(value = "Get questionnnaire model by operation Id ")
	@GetMapping(path = "/operation/{id}/questionnaire")
	public QuestionnaireModelDto getQuestionnaireModelByOperationId(@PathVariable(value = "id") String id){
		LOGGER.info("GET questionnaire for operation with id {}", id);
		return questionnaireModelRepository.findDtoByOperationId(id);
	}
	
	
}
