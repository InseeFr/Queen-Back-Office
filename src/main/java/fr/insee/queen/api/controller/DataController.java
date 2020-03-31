package fr.insee.queen.api.controller;

import java.util.Optional;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.repository.DataRepository;
import io.swagger.annotations.ApiOperation;

/**
* DataController is the Controller using to manage {@link Data} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping
public class DataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);
	
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	@Autowired
	private DataRepository dataRepository;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	*/
	@ApiOperation(value = "Get data by reporting unit Id ")
	@GetMapping(path = "/reporting-unit/{id}/data")
	public DataDto getDataByReportingUnit(@PathVariable(value = "id") Long id){
		LOGGER.info("GET data for reporting unit with id {}", id);
		return dataRepository.findDtoByReportingUnit_id(id);
	}
	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param dataValue	the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* 
	*/
	@ApiOperation(value = "Update data by reporting unit Id ")
	@PutMapping(path = "/reporting-unit/{id}/data")
	public ResponseEntity<Object> setData(@RequestBody JSONObject dataValue, @PathVariable(value = "id") Long id) {
		Optional<Data> dataOptional = dataRepository.findByReportingUnit_id(id);
		if (!dataOptional.isPresent()) {
			LOGGER.info("PUT data for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			dataRepository.updateValue(dataValue.toJSONString().toString(), dataOptional.get().getId());
			LOGGER.info("PUT data for reporting unit with id {} resulting in 200", id);
			return ResponseEntity.ok().build();
		}
	}	
}
