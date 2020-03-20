package fr.insee.queen.queen.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.domain.Data;
import fr.insee.queen.queen.domain.Version;
import fr.insee.queen.queen.dto.operation.DataDto;
import fr.insee.queen.queen.repository.DataRepository;

@RestController
@RequestMapping
public class DataController {
	@Autowired
	private DataRepository dataRepository;
	
	@GetMapping(path = "/reporting-unit/{id}/data")
	public DataDto getDataByReportingUnit(@PathVariable(value = "id") Long id){
		return dataRepository.findDtoByReportingUnit_id(id);
	}
	
	@PutMapping(path = "/reporting-unit/{id}/data")
	public ResponseEntity<Object> setVersion(@PathVariable(value = "id") Long id) {
		Optional<Data> dataOptional = dataRepository.findByReportingUnit_id(id);
		if (!dataOptional.isPresent())
			return ResponseEntity.notFound().build();
		else {
			dataOptional.get().setVersion(Version.COLLECTED);
			dataRepository.save(dataOptional.get());
			return ResponseEntity.ok().build();
		}
	}	
}
