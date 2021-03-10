package fr.insee.queen.api.controller;

import java.util.Optional;

import org.bouncycastle.asn1.cms.MetaData;
import org.json.simple.JSONObject;
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
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.MetadataRepository;
import io.swagger.annotations.ApiOperation;

/**
 * MetadataController is the Controller using to manage {@link MetaData}
 * entity
 * 
 * @author Corcaud Samuel
 * 
 */
@RestController
@RequestMapping(path = "/api")
public class MetadataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	MetadataRepository metadataRepository;
	/**
	* This method is using to get the metadata associated to a specific campaign 
	* 
	* @param id the id of the campaign
	* @return {@link metaData} the metadata associated to the reporting unit
	*/
	@ApiOperation(value = "Get metadata by campaign Id ")
	@GetMapping(path = "/campaign/{id}/metadata")
	public ResponseEntity<Object>  getDataBySurveyUnit(@PathVariable(value = "id") String id){
		Optional<Campaign> campaignOptional = campaignRepository.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 200", id);
			MetadataDto metadataDto = metadataRepository.findDtoByCampaign_id(id);
			if (metadataDto == null) {
				return new ResponseEntity<>(new JSONObject(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(metadataDto.getValue(), HttpStatus.OK);
			}
		}
	}
}
