package fr.insee.queen.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.repository.CampaignRepository;
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

	/**
	* The campaign repository using to access to table 'campaign' in DB 
	*/
	@Autowired
	private CampaignRepository campaignRepository;
	
	/**
	* This method is using to get all campaigns
	* 
	* @return List of all {@link CampaignDto}
	*/
	@ApiOperation(value = "Get list of campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getListCampaign(){
		LOGGER.info("GET campaigns resulting in 200");
		return new ResponseEntity<Object>(campaignRepository.findDtoBy(), HttpStatus.OK);
	}
	
}
