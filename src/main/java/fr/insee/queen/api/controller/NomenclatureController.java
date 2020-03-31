package fr.insee.queen.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.repository.NomenclatureRepository;
import io.swagger.annotations.ApiOperation;

/**
* NomenclatureController is the Controller using to manage {@link Nomenclature} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping
public class NomenclatureController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NomenclatureController.class);
	
	/**
	* The nomencalture repository using to access to table 'nomenclature' in DB 
	*/
	@Autowired
	private NomenclatureRepository nomenclatureRepository;
	
	/**
	* This method is using to get the a specific Nomenclature
	* 
	* @param id the id of nomenclature
	* @return {@link NomenclatureDto} the nomenclature
	*/
	@ApiOperation(value = "Get Nomenclature by Id ")
	@GetMapping(path = "/nomenclature/{id}")
	public NomenclatureDto getNomenclatureById(@PathVariable(value = "id") String id){
		LOGGER.info("GET nomenclature with id {}", id);
		return nomenclatureRepository.findDtoById(id);
	}
	
	/**
	* This method is using to get all nomenclature ids associated to a specific operation 
	* 
	* @param id the id of operation
	* @return List of {@link String} containing nomenclature ids
	*/
	@ApiOperation(value = "Get list of required nomenclature by operation Id ")
	@GetMapping(path = "/operation/{id}/required-nomenclatures")
	public List<String> getListRequiredNomenclature(@PathVariable(value = "id") String id){
		LOGGER.info("GET required-nomenclatures for operation with id {}", id);
		return nomenclatureRepository.findRequiredNomenclatureByOperation(id);
	}
}
