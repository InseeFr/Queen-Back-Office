package fr.insee.queen.api.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.service.NomenclatureService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* NomenclatureController is the Controller using to manage nomenclatures
*
* @author Claudel Benjamin
*
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class NomenclatureController {

	/**
	* The nomencalture repository using to access to table 'nomenclature' in DB
	*/
	private final NomenclatureService nomenclatureService;

	@Operation(summary = "Get all nomenclatures Ids ")
	@GetMapping(path = "/nomenclatures")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<String> getNomenclaturesId() {
		log.info("GET all nomenclatures Ids");
		return nomenclatureService.getAllNomenclatureIds();
	}


	/**
	* This method is using to get the a specific Nomenclature
	*
	* @param nomenclatureId the id of nomenclature
	* @return {@link String} the nomenclature
	*/
	@Operation(summary = "Get Nomenclature by Id ")
	@GetMapping(path = "/nomenclature/{id}")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getNomenclatureById(@IdValid @PathVariable(value = "id") String nomenclatureId){
		log.info("GET nomenclature with id {}", nomenclatureId);
		return nomenclatureService.getNomenclature(nomenclatureId).value();

	}

	/**
	* This method is using to create or update a nomenclature
	*
	* @param nomenclatureInputDto nomenclature to create
	*/
	@Operation(summary = "Post new  or update a nomenclature ")
	@PostMapping(path = "/nomenclature")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	@ResponseStatus(HttpStatus.OK)
	public void postNomenclature(@Valid @RequestBody NomenclatureInputDto nomenclatureInputDto) {
		nomenclatureService.saveNomenclature(nomenclatureInputDto);
	}
}
