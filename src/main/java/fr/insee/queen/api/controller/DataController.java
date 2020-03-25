package fr.insee.queen.api.controller;

import java.util.Optional;

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
import fr.insee.queen.api.domain.Version;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.repository.DataRepository;

/**
* DataController is the Controller using to manage {@link Data} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping
public class DataController {
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
	@GetMapping(path = "/reporting-unit/{id}/data")
	public DataDto getDataByReportingUnit(@PathVariable(value = "id") Long id){
		return dataRepository.findDtoByReportingUnit_id(id);
	}
	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param data	the data to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* 
	*/
	@PutMapping(path = "/reporting-unit/{id}/data")
	public ResponseEntity<Object> setVersion(@RequestBody DataDto data, @PathVariable(value = "id") Long id) {
		Optional<Data> dataOptional = dataRepository.findByReportingUnit_id(id);
		if (!dataOptional.isPresent())
			return ResponseEntity.notFound().build();
		else {
			dataOptional.get().setValue(data.getValue());
			dataOptional.get().setVersion(Version.COLLECTED);
			dataRepository.save(dataOptional.get());
			return ResponseEntity.ok().build();
		}
	}	
}
