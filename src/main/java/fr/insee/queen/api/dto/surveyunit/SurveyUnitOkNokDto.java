package fr.insee.queen.api.dto.surveyunit;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitOkNokDto {
	List<SurveyUnitResponseDto> surveyUnitOK;
	List<SurveyUnitResponseDto> surveyUnitNOK;
	
	public SurveyUnitOkNokDto(List<SurveyUnitResponseDto> surveyUnitOK, List<SurveyUnitResponseDto> surveyUnitNOK) {
		super();
		this.surveyUnitOK = surveyUnitOK;
		this.surveyUnitNOK = surveyUnitNOK;
	}
	
	
	public SurveyUnitOkNokDto() {
		super();
	}


	/**
	 * @return the surveyUnitOK
	 */
	public List<SurveyUnitResponseDto> getSurveyUnitOK() {
		return surveyUnitOK;
	}
	/**
	 * @param surveyUnitOK the surveyUnitOK to set
	 */
	public void setSurveyUnitOK(List<SurveyUnitResponseDto> surveyUnitOK) {
		this.surveyUnitOK = surveyUnitOK;
	}
	/**
	 * @return the surveyUnitNOK
	 */
	public List<SurveyUnitResponseDto> getSurveyUnitNOK() {
		return surveyUnitNOK;
	}
	/**
	 * @param surveyUnitNOK the surveyUnitNOK to set
	 */
	public void setSurveyUnitNOK(List<SurveyUnitResponseDto> surveyUnitNOK) {
		this.surveyUnitNOK = surveyUnitNOK;
	}
	
	
}
