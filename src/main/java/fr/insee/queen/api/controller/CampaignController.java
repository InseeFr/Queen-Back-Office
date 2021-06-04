package fr.insee.queen.api.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.campaign.CampaignResponseDto;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.IntegrationService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* CampaignController is the Controller using to manage {@link Campaign} entity
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
	
	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignService campaignservice;
	
	/**
	* This method is using to get all campaigns
	* 
	* @return List of all {@link CampaignDto}
	*/
	@ApiOperation(value = "Get list of campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getListCampaign(){
		List<CampaignResponseDto> resp = campaignservice.getAllCampaigns();
		LOGGER.info("GET campaigns resulting in 200");
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	/**
	* This method is using to post a new campaign
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
			return ResponseEntity.notFound().build();
		}
		Optional<Campaign> campaignOptional = campaignservice.findById(campaign.getId());
		if (campaignOptional.isPresent()) {
			LOGGER.info("POST campaign with id {} resulting in 400 because it already exists", campaign.getId());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(Boolean.FALSE.equals(campaignservice.checkIfQuestionnaireOfCampaignExists(campaign))) {
			LOGGER.info("POST campaign with id {} resulting in 403 besause a questionnaire does not exist or is already associated ", campaign.getId());
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		campaignservice.saveDto(campaign);
		LOGGER.info("POST campaign with id {} resulting in 200", campaign.getId());
		return ResponseEntity.ok().build();
	}	
	
	/**
	* This method is using to post a new campaign
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
			LOGGER.info("POST campaign context resulting in 400");
			return ResponseEntity.badRequest().build();
		}
		LOGGER.info("POST campaign context resulting in 200");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}	
}
