package fr.insee.queen.api.service.impl;

import java.util.Optional;
import java.util.UUID;

import fr.insee.queen.api.repository.SimpleApiRepository;
import fr.insee.queen.api.service.SurveyUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.domain.SurveyUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.StateDataService;

@Service
public class StateDataServiceImpl extends AbstractService<StateData, UUID> implements StateDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StateDataServiceImpl.class);

    protected final StateDataRepository stateDataRepository;

	@Autowired(required = false)
	private SimpleApiRepository simpleApiRepository;

	@Autowired
	public SurveyUnitService surveyUnitService;

    @Autowired
    public StateDataServiceImpl(StateDataRepository repository) {
        this.stateDataRepository = repository;
    }

    @Override
    protected JpaRepository<StateData, UUID> getRepository() {
        return stateDataRepository;
    }

	@Override
	public void save(StateData stateData) {
		stateDataRepository.save(stateData);
	}

	@Override
	public Optional<StateData> findDtoBySurveyUnitId(String id) {
		return stateDataRepository.findBySurveyUnitId(id);
	}
	
	public ResponseEntity<Object> updateStateData(String id, JsonNode json, SurveyUnit su) {
		Optional<StateData> stateDataOptional = stateDataRepository.findDtoBySurveyUnitId(id);
		StateData stateData;
		if (!stateDataOptional.isPresent()) {
			stateData = new StateData();
			stateData.setSurveyUnit(su);
		}else {
			stateData = stateDataOptional.get();
		}
		updateStateDataFromJson(stateData, json);
		stateDataRepository.save(stateData);
		LOGGER.info("PUT statedata for reporting unit with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
	}

	public ResponseEntity<Object> updateStateData(String id, JsonNode json) {

		if(simpleApiRepository != null){
			LOGGER.info("Method without hibernate");
			simpleApiRepository.updateSurveyUnitStateDate(id, json);
			LOGGER.info("PUT statedata for reporting unit with id {} resulting in 200", id);
			return ResponseEntity.ok().build();
		}
		else {
			LOGGER.info("Method with hibernate");
			Optional<SurveyUnit> su = surveyUnitService.findById(id);
			return updateStateData(id, json, su.get());
		}
	}
	
	public void updateStateDataFromJson(StateData sd, JsonNode json) {
		Long date = json.get("date").longValue();
		String state = json.get("state").textValue();
		String currentPage = json.get("currentPage").textValue();
		if(date != null) {
			sd.setDate(date);
		}
		if(state != null) {
			sd.setState(StateDataType.valueOf(state));
		}
		if(currentPage != null) {
			sd.setCurrentPage(currentPage);
		}
	}

}
