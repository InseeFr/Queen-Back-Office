package fr.insee.queen.api.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.campaign.CampaignResponseDto;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.IntegrationService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* CampaignController is the Controller used to manage {@link Campaign} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class CampaignController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);

	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private IntegrationService integrationService;

	@Autowired
	public Consumer<String> evictCampaignFromCache;

	@Value("${fr.insee.queen.pilotage.integration.override:#{null}}")
	private String integrationOverride;

	/**
	* The campaign repository used to access 'campaign' table in DB 
	*/
	@Autowired
	private CampaignService campaignservice;
	
	@Autowired
	private QuestionnaireModelService questionnaireService;
	
	/**
	* This method is used to get all campaigns
	* 
	* @return List of all {@link CampaignDto}
	*/
	@ApiOperation(value = "Get list of all campaigns")
	@GetMapping(path = "/admin/campaigns")
	public ResponseEntity<Object> getListCampaign(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		LOGGER.info("Admin {} request all campaigns", userId);
		List<CampaignResponseDto> resp = campaignservice.getAllCampaigns();
		LOGGER.info("GET all campaigns resulting in 200. {} campaign(s) found for {}", resp.size(), userId);
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/**
	* This method return all user related campaigns
	* 
	* @return List of  {@link CampaignDto}
	*/
	@ApiOperation(value = "Get list of user related campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getInterviewerCampaignList(HttpServletRequest request) {

		String userId = utilsService.getUserId(request);
		LOGGER.info("User {} need his campaigns", userId);

		List<CampaignResponseDto> completeCampaigns;

		if (integrationOverride != null && integrationOverride.equals("true")) {
			completeCampaigns = campaignservice.getAllCampaigns();
		} else {

			List<CampaignResponseDto> campaigns = utilsService.getInterviewerCampaigns(request);

			// add questionnaireId
			completeCampaigns = campaigns.stream().map(camp -> {
				camp.setQuestionnaireIds(questionnaireService.findAllQuestionnaireIdDtoByCampaignId(camp.getId()));
				return camp;
			}).collect(Collectors.toList());

		}

		LOGGER.info("GET campaigns resulting in 200. {} campaign(s) found for {}", completeCampaigns.size(), userId);
		return new ResponseEntity<>(completeCampaigns, HttpStatus.OK);
	}
	
	/**
	* This method is used to post a new campaign
	* 
	* @param campaign the value to create
	* @return {@link HttpStatus 400} if questionnaire is not found, else {@link HttpStatus 200} 
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Create a campaign")
	@PostMapping(path = "/campaigns")
	public ResponseEntity<Object> createCampaign(@RequestBody CampaignDto campaign, HttpServletRequest request) {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			LOGGER.info("Access restricted to profiles : TEST / DEV");
			return ResponseEntity.notFound().build();
		}
		String campaignId = campaign.getId().toUpperCase();
		Optional<Campaign> campaignOptional = campaignservice.findById(campaignId);
		if (campaignOptional.isPresent()) {
			LOGGER.error("POST campaign with id {} resulting in 400 because it already exists", campaignId);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}else{
			// prevent campaign cache from always returning empty Optional from previous call
			evictCampaignFromCache.accept(campaignId);
		}
		if(Boolean.FALSE.equals(campaignservice.checkIfQuestionnaireOfCampaignExists(campaign))) {
			LOGGER.error("POST campaign with id {} resulting in 403 besause a questionnaire does not exist or is already associated ", campaignId);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		campaignservice.saveDto(campaign);
		LOGGER.info("POST campaign with id {} resulting in 200", campaignId);
		return ResponseEntity.ok().build();
	}	
	
	/**
	* This method is used to post a new campaign
	* 
	* @param campaign the value to create
	* @return {@link HttpStatus 400} if questionnaire is not found, else {@link HttpStatus 200}
	 * @throws Exception 
	 * @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Integrates the context of a campaign")
	@PostMapping(path = "/campaign/context")
	public ResponseEntity<Object> integrateContext(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		IntegrationResultDto result = null;
		try {
			result = integrationService.integrateContext(file);
		} catch(IOException | XPathExpressionException | SAXException | ParserConfigurationException e) {
			LOGGER.error("POST campaign context resulting in 400");
			return ResponseEntity.badRequest().build();
		}
		LOGGER.info("POST campaign context resulting in 200");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	* This method is used to delete a campaign
	* 
	* @param campaign the value to delete
	* @return {@link HttpStatus}
	* 
	*/
	@ApiOperation(value = "Delete a campaign")
	@DeleteMapping(path = "/campaign/{id}")
	public ResponseEntity<Object> deleteCampaignById(@RequestParam("force") Boolean force,HttpServletRequest request, @PathVariable(value = "id") String id) {
		Boolean isDeletable=false;
		Optional<Campaign> campaignOptional = campaignservice.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if(force || (integrationOverride != null && integrationOverride.equals("true")))
		{
			isDeletable=true;
		}else {
			try {
				isDeletable = campaignservice.isClosed(campaignOptional.get(),request);
			} catch(RestClientException e) {
				LOGGER.error("Error when requesting pilotage API");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}
		if(isDeletable){
			campaignservice.delete(campaignOptional.get());
			LOGGER.info("DELETE campaign with id {} resulting in 200", id);
			return ResponseEntity.ok().build();
		}else{
			LOGGER.info("Unable to delete campaign {}, campaign isn't closed", id);
			return ResponseEntity.unprocessableEntity().build();
		}

	}	
}
