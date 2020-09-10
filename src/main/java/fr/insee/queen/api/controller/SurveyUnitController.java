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

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* SurveyUnitController is the Controller using to manage {@link SurveyUnit} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class SurveyUnitController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitController.class);

	/**
	* The survey unit repository using to access to table 'survey_unit' in DB 
	*/
	@Autowired
	private SurveyUnitRepository surveyUnitRepository;
	
	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignRepository campaignRepository;
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* This method is using to get all survey units associated to a specific campaign 
	* 
	* @param id the id of campaign
	* @return List of {@link SurveyUnitDto}
	 * @throws ParseException 
	 * @throws IOException 
	*/
	@ApiOperation(value = "Get list of survey units by camapign Id ")
	@GetMapping(path = "/campaign/{id}/survey-units")
	public ResponseEntity<Object> getListSurveyUnitByCampaign(HttpServletRequest request, @PathVariable(value = "id") String id) throws ParseException, IOException{
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST")) {
			Optional<Campaign> campaignOptional = campaignRepository.findById(id);
			if (!campaignOptional.isPresent()) {
				LOGGER.info("GET survey-units for campaign with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}
			Map<String, SurveyUnitDto> surveyUnitMap = new HashMap<>();
			ResponseEntity<Object> result = utilsService.getSuFromPearlJam(request);
			LOGGER.info("GET survey-units from PearJam API resulting in {}", result.getStatusCode());
			if(result.getStatusCode()!=HttpStatus.OK) {
				LOGGER.error("GET survey-units for campaign with id {} resulting in 500"
						+ "caused by one of following: \n"
						+ "- No survey unit found in pearl jam DB \n"
						+ "- User not authorized ", id);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String,String>> objects = (List<LinkedHashMap<String, String>>) result.getBody();
			if(objects.isEmpty()) {
				LOGGER.info("GET survey-units for campaign with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}
			LOGGER.info("Number of SU read in Pearl Jam API : {}", objects.size());
			LOGGER.info("Detail : {}", displayDetail(objects));
			for(LinkedHashMap<String, String> map : objects) {
				if(map.get("campaign").equals(id)) {
					SurveyUnitDto su = surveyUnitRepository.findDtoById(map.get("id"));
					if(su != null && surveyUnitMap.get(su.getId())==null) {
						surveyUnitMap.put(su.getId(), su);
					}
				}
			}
			LOGGER.info("Number of SU to return : {}", surveyUnitMap.size());
			LOGGER.info("GET survey-units for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(surveyUnitMap.values(), HttpStatus.OK);			
		} else {
			LOGGER.info("GET survey-units for campaign with id {} resulting in 200", id);
			List<SurveyUnitDto> results = surveyUnitRepository.findDtoByCampaign_id(id);
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
