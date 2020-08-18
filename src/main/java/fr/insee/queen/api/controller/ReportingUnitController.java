package fr.insee.queen.api.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
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
import fr.insee.queen.api.domain.ReportingUnit;
import fr.insee.queen.api.dto.reportingunit.ReportingUnitDto;
import fr.insee.queen.api.repository.OperationRepository;
import fr.insee.queen.api.repository.ReportingUnitRepository;
import fr.insee.queen.api.service.UtilsService;
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
	* The operation repository using to access to table 'operation' in DB 
	*/
	@Autowired
	private OperationRepository operationRepository;
	
	/**
	* The operation repository using to access to table 'operation' in DB 
	*/
	@Autowired
	private UtilsService utilsService;
	
	/**
	* This method is using to get all reporting units associated to a specific operation 
	* 
	* @param id the id of operation
	* @return List of {@link ReportingUnitDto}
	 * @throws ParseException 
	 * @throws IOException 
	*/
	@ApiOperation(value = "Get list of reporting units by operation Id ")
	@GetMapping(path = "/operation/{id}/reporting-units")
	public ResponseEntity<Object> getListReportingUnitByOperation(HttpServletRequest request, @PathVariable(value = "id") String id) throws ParseException, IOException{
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST")) {
			Optional<Operation> operationOptional = operationRepository.findById(id);
			if (!operationOptional.isPresent()) {
				LOGGER.info("GET reporting-units for operation with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}
			Map<String, ReportingUnitDto> reportingUnitMap = new HashMap<>();
			ResponseEntity<Object> result = utilsService.getSuFromPearlJam(request);
			LOGGER.info("GET survey-units from PearJam API resulting in {}", result.getStatusCode());
			if(result.getStatusCode()!=HttpStatus.OK) {
				LOGGER.error("GET reporting-units for operation with id {} resulting in 500"
						+ "caused by one of following: \n"
						+ "- No survey unit found in pearl jam DB \n"
						+ "- User not authorized ", id);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			List<LinkedHashMap<String,String>> objects = (List<LinkedHashMap<String, String>>) result.getBody();
			if(objects.isEmpty()) {
				LOGGER.info("GET reporting-units for operation with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}
			LOGGER.info("Number of SU read in Pearl Jam API : {}", objects.size());
			LOGGER.info("Detail : {}", displayDetail(objects));
			for(LinkedHashMap<String, String> map : objects) {
				if(map.get("campaign").equals(id)) {
					ReportingUnitDto ru = reportingUnitRepository.findDtoById(map.get("id"));
					if(ru != null && reportingUnitMap.get(ru.getId())==null) {
						reportingUnitMap.put(ru.getId(), ru);
					}
				}
			}
			LOGGER.info("Number of SU to return : {}", reportingUnitMap.size());
			LOGGER.info("GET reporting-units for operation with id {} resulting in 200", id);
			return new ResponseEntity<>(reportingUnitMap.values(), HttpStatus.OK);			
		} else {
			LOGGER.info("GET reporting-units for operation with id {} resulting in 200", id);
			List<ReportingUnitDto> results = reportingUnitRepository.findDtoByOperation_id(id);
			if(results.isEmpty()) {
				return ResponseEntity.notFound().build();
			}else {
				return new ResponseEntity<>(results, HttpStatus.OK);
			}
		}
	}

	private String displayDetail(List<LinkedHashMap<String, String>> objects) {
		Map<String,Integer> nbSUbyCampaign = new HashMap<>();
		for(LinkedHashMap<String, String> map : objects) {
			if(nbSUbyCampaign.get(map.get("campaign"))==null) {
				nbSUbyCampaign.put(map.get("campaign"), 0);
			}
			nbSUbyCampaign.put(map.get("campaign"),  nbSUbyCampaign.get(map.get("campaign"))+1);
		}
		return "["+nbSUbyCampaign.entrySet()
	            .stream()
	            .map(entry -> entry.getKey() + ": " + entry.getValue() + " Suvey unit")
	            .collect(Collectors.joining("; "))+"]";

	}
}
