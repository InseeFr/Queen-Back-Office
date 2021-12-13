package fr.insee.queen.api.service.impl;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.repository.SimpleApiRepository;
import fr.insee.queen.api.service.SurveyUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.SurveyUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.DataService;

@Service
public class DataServiceImpl extends AbstractService<Data, UUID> implements DataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataServiceImpl.class);

    protected final DataRepository dataRepository;

	@Autowired
	public SurveyUnitService surveyUnitService;

    @Autowired
    public DataServiceImpl(DataRepository repository) {
        this.dataRepository = repository;
    }

	@Autowired(required = false)
	private SimpleApiRepository simpleApiRepository;

    @Override
    protected JpaRepository<Data, UUID> getRepository() {
        return dataRepository;
    }

	@Override
	public Optional<Data> findBySurveyUnitId(String id) {
		return dataRepository.findBySurveyUnitId(id);
	}

	@Override
	public void save(Data comment) {
		dataRepository.save(comment);
	}
	
	public void updateData(SurveyUnit su, JsonNode dataValue) {
		Optional<Data> dataOptional = dataRepository.findBySurveyUnitId(su.getId());
		if (!dataOptional.isPresent()) {
			Data newData = new Data();
			newData.setSurveyUnit(su);
			newData.setValue(dataValue);
			dataRepository.save(newData);
			
		}else {
			Data data = dataOptional.get();
			data.setValue(dataValue);
			dataRepository.save(data);
		}
	}

	@Override
	public void updateDataImproved(String id, JsonNode dataValue) {
		if(simpleApiRepository != null){
			LOGGER.info("Method without hibernate");
			simpleApiRepository.updateSurveyUnitData(id, dataValue);
		}
		else {
			LOGGER.info("Method with hibernate");
			Optional<SurveyUnit> su = surveyUnitService.findById(id);
			updateData(su.get(),dataValue);
		}
	}



}
