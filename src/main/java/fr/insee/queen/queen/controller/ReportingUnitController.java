package fr.insee.queen.queen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.queen.dto.operation.ReportingUnitDto;
import fr.insee.queen.queen.repository.ReportingUnitRepository;

@RestController
@RequestMapping
public class ReportingUnitController {
	@Autowired
	private ReportingUnitRepository reportingUnitRepository;
	
	@GetMapping(path = "/operation/{id}/reporting-units")
	public List<ReportingUnitDto> listReportingUnitByOperation(@PathVariable(value = "id") String id){
		return reportingUnitRepository.findDtoByOperation_id(id);
	}
	
	
}
