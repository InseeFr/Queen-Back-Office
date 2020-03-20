package fr.insee.queen.queen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.dto.operation.NomenclatureDto;
import fr.insee.queen.queen.repository.NomenclatureRepository;

@RestController
@RequestMapping
public class NomenclatureController {
	@Autowired
	private NomenclatureRepository nomenclatureRepository;
	
	@GetMapping(path = "/nomenclature/{id}")
	public NomenclatureDto getNomenclatureById(@PathVariable(value = "id") String id){
		return nomenclatureRepository.findNomenclatureById(id);
	}
	
	@GetMapping(path = "/operation/{id}/required-nomenclatures")
	public List<String> getListRequiredNomenclature(@PathVariable(value = "id") String id){
		return nomenclatureRepository.findRequiredNomenclatureByOperation(id);
	}
}
