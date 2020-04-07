package fr.insee.queen.api.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Operation;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.repository.OperationRepository;
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
	* The operation repository using to access to table 'operation' in DB 
	*/
	@Autowired
	private OperationRepository operationRepository;
	
	/**
	* This method is using to get the questionnaireModel associated to a specific operation 
	* 
	* @param id the id of operation
	* @return the {@link QuestionnaireModelDto} associated to the operation
	*/
	@ApiOperation(value = "Get questionnnaire model by operation Id ")
	@GetMapping(path = "/operation/{id}/questionnaire")
	public ResponseEntity<Object> getQuestionnaireModelByOperationId(@PathVariable(value = "id") String id){
		Optional<Operation> operationOptional = operationRepository.findById(id);
		if (!operationOptional.isPresent()) {
			LOGGER.info("GET questionnaire for operation with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET questionnaire for operation with id {} resulting in 200", id);
			return new ResponseEntity<Object>(questionnaireModelRepository.findDtoByOperationId(id), HttpStatus.OK);
		}
	}
	
	
}
