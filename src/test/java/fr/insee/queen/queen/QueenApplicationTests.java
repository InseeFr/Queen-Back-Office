package fr.insee.queen.queen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import fr.insee.queen.queen.domain.Nomenclature;
import fr.insee.queen.queen.domain.Comment;
import fr.insee.queen.queen.domain.Data;
import fr.insee.queen.queen.domain.Operation;
import fr.insee.queen.queen.domain.QuestionnaireModel;
import fr.insee.queen.queen.domain.ReportingUnit;

class QueenApplicationTests extends AbstractTests {

	@Test
	public void testFindOperation() {
		login("admin");
		var result = get("/operations", Operation[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.getId().equals("TRVTST")):"Operation 'TRVTST' should exist";
	}
	
	@Test
	public void testFindQuestionnaireByOperation() {
		login("admin");
		var result = get("/operation/MOBTST/questionnaire", QuestionnaireModel.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getModel())):"QuestionnaireModel 'MOBTST' should have jsonb model";
	}
	
	@Test
	public void testFindReportUnitsByOperation() {
		login("admin");
		var result = get("/operation/MOBTST/reporting-units", ReportingUnit[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.getId()==1):"Reporting unit '1' should exist";
	}
	
	@Test
	public void testFindNomenclatureById() {
		login("admin");
		var result = get("/nomenclature/MOBTST", Nomenclature.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Nomenclature 'MOBTST' should have jsonb model";
	}
	
	@Test
	public void testFindCommentByOperation(){
		login("admin");
		var result = get("/reporting-unit/1/comment", Comment.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Comment for reporting unit '1' should exist";
	}
	
	@Test
	public void testFindDataByOperation(){
		login("admin");
		var result = get("/reporting-unit/1/data", Data.class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> StringUtils.isNotBlank(o.getValue())):"Data for reporting unit '1' should exist";
	}
	
	@Test
	public void testFindRequiredNomenclatureByOperation(){
		login("admin");
		var result = get("/operation/MOBTST/required-nomenclatures", String[].class);
		assert	result.getStatusCode().is2xxSuccessful():"error status will be 200";
		assert List.of(result.getBody()).stream().anyMatch(o -> o.equals("MOBTST")):"required nomenclature 'MOBTST' should exist";
	}
	
	
	
}
