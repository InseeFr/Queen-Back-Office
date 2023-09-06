package fr.insee.queen.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import fr.insee.queen.api.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitDto(
	String id,
	String questionnaireId,
	@JsonRawValue String personalization,
	@JsonRawValue String data,
	@JsonRawValue String comment,
	StateDataDto stateData) {

	public static SurveyUnitDto createSurveyUnitNOKDto(String id) {
		return new SurveyUnitDto(id, null, null, null, null, null);
	}

	public static SurveyUnitDto createSurveyUnitOKDtoWithStateData(String id, StateDataDto stateDataDto) {
		return new SurveyUnitDto(id, null, null, null, null, stateDataDto);
	}

	public static SurveyUnitDto createSurveyUnitOKDtoWithQuestionnaireModel(String id, String questionnaireModelId) {
		return new SurveyUnitDto(id, questionnaireModelId, null, null, null, null);
	}
}
