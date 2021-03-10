package fr.insee.queen.api.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.Personnalization;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateStateData;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.campaign.CampaignResponseDto;
import fr.insee.queen.api.dto.stateData.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.pdfutils.ExportPdf;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
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
	private QuestionnaireModelRepository questionnaireRepository;
	
	@Autowired
	private UtilsService utilsService;
	
	
	/**
	* This method is used to get a survey-unit by id
	*/
	@ApiOperation(value = "Get survey-unit")
	@GetMapping(path = "/survey-unit/{id}")
	public ResponseEntity<Object> getSurveyUnitById(HttpServletRequest request, @PathVariable(value = "id") String id) throws ParseException, IOException{
		Optional<SurveyUnit> suOpt = surveyUnitRepository.findById(id);
		if(!suOpt.isPresent()) {
			LOGGER.info("GET survey-units with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		SurveyUnit su = suOpt.get();
		SurveyUnitResponseDto resp = new SurveyUnitResponseDto(
				null, 
				null, 
				su.getPersonnalization()==null ? null :su.getPersonnalization().getValue(), 
				su.getData()==null ? null : su.getData().getValue(), 
				new StateDataDto(su.getStateData()));
		LOGGER.info("GET survey-unit resulting in 200");
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}
	
	
	
	/**
	* This method is used to update a survey-unit by id
	*/
	@ApiOperation(value = "Put survey-unit")
	@PutMapping(path = "/survey-unit/{id}")
	public ResponseEntity<Object> getSurveyUnitById(@RequestBody JSONObject surveyUnit, HttpServletRequest request, @PathVariable(value = "id") String id) throws ParseException, IOException{
		Optional<SurveyUnit> su = surveyUnitRepository.findById(id);
		if(!su.isPresent()) {
			LOGGER.info("PUT survey-unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		
		try {
			SurveyUnit newSU = su.get();
			
			JSONArray personnalization = null;
			JSONObject data = null;
			JSONObject statedata = null;
			if(surveyUnit.get("personnalization") != null) {
				personnalization = new JSONArray();
				
				personnalization.addAll(
						(List<JSONObject>)((List) surveyUnit.get("personnalization")).stream()
						.map(o -> new JSONObject((Map)o))
						.collect(Collectors.toList())
				);
				
				LOGGER.info(personnalization.toJSONString());
				if(newSU.getPersonnalization()!=null) {
					newSU.getPersonnalization().setValue(personnalization);
				}
				else {
					Personnalization pers = new Personnalization();
					pers.setValue(personnalization);
					pers.setSurveyUnit(newSU);
					newSU.setPersonnalization(pers);
				}

			}
			if(surveyUnit.get("data") != null) {
				data = new JSONObject((Map) surveyUnit.get("data"));
				if(newSU.getData()!=null) {
					newSU.getData().setValue(data);
				}
				else {
					Data dat = new Data();
					dat.setValue(data);
					dat.setSurveyUnit(newSU);
					newSU.setData(dat);
				}
			}
			if(surveyUnit.get("stateData") != null) {
				statedata = new JSONObject((Map) surveyUnit.get("stateData"));
				
				Long date = (Long) statedata.get("date");
				String state = (String) statedata.get("state");
				Integer currentPage = (Integer) statedata.get("currentPage");
				
				if(newSU.getStateData()!=null) {
					if(date != null) {
						newSU.getStateData().setDate(date);
					}
					if(state != null) {
						newSU.getStateData().setState(StateStateData.valueOf(state));
					}
					if(currentPage != null) {
						newSU.getStateData().setCurrentPage(currentPage);
					}
				}
				else {
					StateData statedat = new StateData();
					if(date != null) {
						statedat.setDate(date);
					}
					if(state != null) {
						statedat.setState(StateStateData.valueOf(state));
					}
					if(currentPage != null) {
						statedat.setCurrentPage(currentPage);
					}
					statedat.setSurveyUnit(newSU);
					newSU.setStateData(statedat);
				}
			}
			
			surveyUnitRepository.save(newSU);
		}
		catch(Exception e) {
			LOGGER.info(e.getMessage());
			LOGGER.info("PUT survey-units resulting in 400");
			return ResponseEntity.badRequest().build();
		}
		
		
		
		LOGGER.info("PUT survey-units resulting in 200");
		return ResponseEntity.ok().build();
	}
	
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
			Map<String, SurveyUnitResponseDto> surveyUnitMap = new HashMap<>();
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
					LOGGER.info("ID : {}", map.get("id"));
					Optional<SurveyUnit> su = surveyUnitRepository.findById(map.get("id"));
					if(su.isPresent() && surveyUnitMap.get(su.get().getId())==null) {
						LOGGER.info("ID is present");
						surveyUnitMap.put(su.get().getId(), new SurveyUnitResponseDto(su.get().getId(), su.get().getQuestionnaireModelId(), null, null, null));
					}
				}
			}
			LOGGER.info("Number of SU to return : {}", surveyUnitMap.size());
			LOGGER.info("GET survey-units for campaign with id {} resulting in 200", id);
			return new ResponseEntity<>(surveyUnitMap.values(), HttpStatus.OK);			
		} else {
			LOGGER.info("GET survey-units for campaign with id {} resulting in 200", id);
			List<SurveyUnit> results = surveyUnitRepository.findByCampaign_id(id);
			List<SurveyUnitResponseDto> resp = results.stream()
					.map(su -> new SurveyUnitResponseDto(su.getId(), su.getQuestionnaireModelId(),null, null, null))
					.collect(Collectors.toList());
			return new ResponseEntity<>(resp, HttpStatus.OK);
			
		}
	}
	
	@ApiOperation(value = "Get deposit proof for a SU ")
	@RequestMapping(value = "/survey-unit/{id}/deposit-proof", method = RequestMethod.GET)
	public ResponseEntity<Object> getDepositProof(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException{
		
		Optional<SurveyUnit> suOpt = surveyUnitRepository.findById(id);
		if(!suOpt.isPresent()) {
			LOGGER.info("GET deposit-proof with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		else {
			SurveyUnit su = suOpt.get();
			if (su.getStateData()!=null) {
				String userId = utilsService.getUserId(request);
				String campaignId = su.getCampaign().getId();
				String campaignLabel = su.getCampaign().getLabel();
				String date = "";
				if(su.getStateData().getState().equals(StateStateData.EXPORTED)) {
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm"); 
					date = dateFormat.format(new Date(su.getStateData().getDate()));
				}
				ExportPdf exp = new ExportPdf();
				try {
					exp.doExport(response, date, campaignId, campaignLabel, userId);
				} catch (ServletException | IOException e) {
					return new ResponseEntity<>(new JSONObject(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return ResponseEntity.ok().build();
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
