package fr.insee.queen.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}
	
	// User
	public static final String GUEST = "GUEST";
	
	//API url for endpoints
	public static final String API_OPERATIONS = "/api/operations";
	public static final String API_OPERATIONS_REPORTING_UNITS = "/api/operation/{idOperation}/reporting-units";
	public static final String API_OPERATIONS_QUESTIONAIRE = "/api/operation/{idOperation}/questionnaire";
	public static final String API_OPERATIONS_REQUIRED_NOMENCLATURE = "/api/operation/{id}/required-nomenclatures";
	public static final String API_REPORTING_UNIT_DATA = "/api/reporting-unit/{id}/data";
	public static final String API_REPORTING_UNIT_COMMENT = "/api/reporting-unit/{id}/comment";
	public static final String API_NOMENCLATURE = "/api/nomenclature/{id}";
}
