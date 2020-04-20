package fr.insee.queen.api.controller;

import java.sql.SQLException;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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
import fr.insee.queen.api.domain.ReportingUnit;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.repository.ReportingUnitRepository;
import io.swagger.annotations.ApiOperation;

/**
* DataController is the Controller using to manage {@link Data} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class DataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);
	
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	@Autowired
	private DataRepository dataRepository;
	
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private ReportingUnitRepository reportingUnitRepository;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	*/
	@ApiOperation(value = "Get data by reporting unit Id ")
	@GetMapping(path = "/reporting-unit/{id}/data")
	public ResponseEntity<Object>  getDataByReportingUnit(@PathVariable(value = "id") String id){
		Optional<ReportingUnit> reportingUnitOptional = reportingUnitRepository.findById(id);
		if (!reportingUnitOptional.isPresent()) {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 200", id);
			Optional<Data> dataOptional = dataRepository.findByReportingUnit_id(id);
			if (!dataOptional.isPresent()) {
				return new ResponseEntity<>(new JSONObject(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(dataOptional.get().getValue(), HttpStatus.OK);
			}
		}
	}
	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param dataValue	the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if data is not found, else {@link HttpStatus 200}
	* 
	*/
	@ApiOperation(value = "Update data by reporting unit Id ")
	@PutMapping(path = "/reporting-unit/{id}/data")
	public ResponseEntity<Object> setData(@RequestBody JSONObject dataValue, @PathVariable(value = "id") String id) throws ParseException, SQLException {
		Optional<ReportingUnit> reportingUnitOptional = reportingUnitRepository.findById(id);
		if (!reportingUnitOptional.isPresent()) {
			LOGGER.info("PUT data for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			Optional<Data> dataOptional = dataRepository.findByReportingUnit_id(id);
			if (!dataOptional.isPresent()) {
				LOGGER.info("PUT data for reporting unit with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}else {
				dataOptional.get().setValue(dataValue);
				dataRepository.save(dataOptional.get());
				LOGGER.info("PUT data for reporting unit with id {} resulting in 200", id);
				return ResponseEntity.ok().build();
			}
		}
	}
}
