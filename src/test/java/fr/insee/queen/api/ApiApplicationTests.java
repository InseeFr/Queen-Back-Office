package fr.insee.queen.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.Operation;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.ReportingUnit;
import fr.insee.queen.api.domain.Version;
import fr.insee.queen.api.repository.CommentRepository;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.repository.ReportingUnitRepository;

class ApiApplicationTests extends AbstractTests {
	
	@Autowired
	ReportingUnitRepository reportingUnitRepository;
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	DataRepository dataRepository;

	@Test
	public void testFindOperation() {
		login("admin");
		var result = get("/operations", Operation[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.getId().equals("simpsons2020x00")):"Operation 'simpsons2020x00' should exist";
	}
	
	@Test
	public void testFindQuestionnaireByOperation() {
		login("admin");
		var result = get("/operation/simpsons2020x00/questionnaire", QuestionnaireModel.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getModel())):"QuestionnaireModel 'simpsons' should have jsonb model";
	}
	
	@Test
	public void testFindReportUnitsByOperation() {
		login("admin");
		var result = get("/operation/simpsons2020x00/reporting-units", ReportingUnit[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.getId()==11):"Reporting unit '11' should exist";
	}
	
	@Test
	public void testFindNomenclatureById() {
		login("admin");
		var result = get("/nomenclature/cities2019", Nomenclature.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Nomenclature 'cities2019' should have jsonb model";
	}
	
	@Test
	public void testFindCommentByReportingUnit(){
		login("admin");
		var result = get("/reporting-unit/22/comment", Comment.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Comment for reporting unit '22' should exist";
	}
	
	@Test
	public void testPutCommentByReportingUnit(){
		login("admin");
		ReportingUnit rutest = reportingUnitRepository.getOne((long)22);
		String value =  "{\"EXTERNAL\": {\"LAST_BROADCAST\": \"12/07/1998\"}}";
		var commentInit = commentRepository.findDtoByReportingUnit_id(rutest.getId());
		var put = put("/reporting-unit/22/comment", "{\"EXTERNAL\": {\"LAST_BROADCAST\": \"12/07/1998\"}}", String.class);
		assert put.getStatusCode().is2xxSuccessful():"Update a comment should be a success";
		var commentRepo = commentRepository.findDtoByReportingUnit_id(rutest.getId());
		System.out.println("commentRepo.getvalue : " + commentRepo.getValue());
		System.out.println("value : " + value);
		assert !commentRepo.getValue().equals(commentInit.getValue()): "Value should should be updated";
	}
	
	@Test
	public void testFindDataByReportingUnit(){
		login("admin");
		var result = get("/reporting-unit/22/data", Data.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Data for reporting unit '22' should exist";
	}
	
	@Test
	public void testPutDataByReportingUnit(){
		login("admin");
		ReportingUnit rutest = reportingUnitRepository.getOne((long)22);
		String value =  "{\"EXTERNAL\": {\"LAST_BROADCAST\": \"12/07/1998\"}}";
		var dataInit = dataRepository.findDtoByReportingUnit_id(rutest.getId());
		var put = put("/reporting-unit/22/data", "{\"EXTERNAL\": {\"LAST_BROADCAST\": \"12/07/1998\"}}", String.class);
		assert put.getStatusCode().is2xxSuccessful():"Update a comment should be a success";
		var dataRepo = dataRepository.findDtoByReportingUnit_id(rutest.getId());
		System.out.println("dataRepo.getValue() : " +  dataRepo.getValue());
		System.out.println("dataRepo.getVersion() : " +  dataRepo.getVersion());
		System.out.println("value : " + value);
		assert !dataRepo.getValue().equals(dataInit.getValue()): "Value should should be updated";
		assert dataRepo.getVersion() == Version.COLLECTED: "Version should be COLLECTED";
	}
	
	@Test
	public void testFindRequiredNomenclatureByOperation(){
		login("admin");
		var result = get("/operation/vqs2021x00/required-nomenclatures", String[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.equals("cities2019")):"required nomenclature 'cities2019' should exist";
	}
	
}
