package fr.insee.queen.api.service.impl;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
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

    protected final DataRepository dataRepository;

    @Autowired
    public DataServiceImpl(DataRepository repository) {
        this.dataRepository = repository;
    }

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

}
