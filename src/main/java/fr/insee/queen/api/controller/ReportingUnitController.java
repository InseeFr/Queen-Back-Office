package fr.insee.queen.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.ReportingUnit;
import fr.insee.queen.api.dto.reportingunit.ReportingUnitDto;
import fr.insee.queen.api.repository.ReportingUnitRepository;
import io.swagger.annotations.ApiOperation;

/**
* ReportingUnitController is the Controller using to manage {@link ReportingUnit} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class ReportingUnitController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingUnitController.class);

	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private ReportingUnitRepository reportingUnitRepository;
	
	/**
	* This method is using to get all reporting units associated to a specific operation 
	* 
	* @param id the id of operation
	* @return List of {@link ReportingUnitDto}
	*/
	@ApiOperation(value = "Get list of reporting units by operation Id ")
	@GetMapping(path = "/operation/{id}/reporting-units")
	public List<ReportingUnitDto> getListReportingUnitByOperation(@PathVariable(value = "id") String id){
		LOGGER.info("GET reporting-units for operation with id {}", id);
		return reportingUnitRepository.findDtoByOperation_id(id);
	}
	
	
}
