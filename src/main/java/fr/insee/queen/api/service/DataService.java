package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.SurveyUnit;

public interface DataService extends BaseService<Data, UUID> {

	Optional<Data> findBySurveyUnitId(String id);

	void save(Data comment);
	
	public void updateData(SurveyUnit su, JsonNode dataValue);

	void updateDataImproved(String id, JsonNode dataValue);

}
