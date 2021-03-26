package fr.insee.queen.api.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.domain.Version;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.exception.BadRequestException;
import fr.insee.queen.api.pdfutils.ExportPdf;
import fr.insee.queen.api.repository.ApiRepository;
import fr.insee.queen.api.repository.CommentRepository;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.repository.PersonalizationRepository;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.CommentService;
import fr.insee.queen.api.service.DataService;
import fr.insee.queen.api.service.PersonalizationService;
import fr.insee.queen.api.service.StateDataService;
import fr.insee.queen.api.service.SurveyUnitService;
import fr.insee.queen.api.service.UtilsService;

@Service
public class SurveyUnitServiceImpl extends AbstractService<SurveyUnit, String> implements SurveyUnitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitServiceImpl.class);
	
    protected final SurveyUnitRepository surveyUnitRepository;
    
    @Autowired
	private StateDataService stateDataService;
    
    @Autowired
	private DataService dataService;
        
    @Autowired
	private CommentService commentService;
    
    @Autowired
	private PersonalizationService personalizationService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private CampaignService campaignService;
	
	@Autowired
	private StateDataRepository stateDataRepository;
	
	@Autowired
	private DataRepository dataRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PersonalizationRepository personalizationRepository;


    @Autowired
    public SurveyUnitServiceImpl(SurveyUnitRepository repository) {
        this.surveyUnitRepository = repository;
    }

    @Override
    protected ApiRepository<SurveyUnit, String> getRepository() {
        return surveyUnitRepository;
    }

	@Override
	public Optional<SurveyUnit> findById(String id) {
		return surveyUnitRepository.findById(id);
	}

	@Override
	public SurveyUnitDto findDtoById(String id) {
		return surveyUnitRepository.findDtoById(id);
	}

	@Override
	public List<SurveyUnitDto> findDtoByCampaignId(String id) {
		return surveyUnitRepository.findDtoByCampaignId(id);
	}

	@Override
	public void save(SurveyUnit newSU) {
		surveyUnitRepository.save(newSU);
	}

	@Override
	public List<SurveyUnit> findByCampaignId(String id) {
		return surveyUnitRepository.findByCampaignId(id);
	}

	@Override
	@Transactional
	public void updateSurveyUnit(SurveyUnit newSU, JsonNode surveyUnit) {
			if(surveyUnit.get("personalization") != null) {
				this.updatePersonalization(newSU, surveyUnit);
			}
			if(surveyUnit.get("comment") != null) {
				this.updateComment(newSU, surveyUnit);
			}
			if(surveyUnit.get("data") != null) {
				this.updateData(newSU, surveyUnit);
			}
			if(surveyUnit.get("stateData") != null) {
				this.updateStateData(newSU, surveyUnit);
			}
			surveyUnitRepository.save(newSU);
			personalizationRepository.save(newSU.getPersonalization());
			commentRepository.save(newSU.getComment());
			dataRepository.save(newSU.getData());
			stateDataRepository.save(newSU.getStateData());
		}
	
	private void updateStateData(SurveyUnit newSU, JsonNode surveyUnit) {
		JsonNode statedata = surveyUnit.get("stateData");
		if(newSU.getStateData()!=null) {
			stateDataService.updateStateDataFromJson(newSU.getStateData(), statedata);
		} else {
			StateData statedat = new StateData();
			stateDataService.updateStateDataFromJson(statedat, statedata);
			statedat.setSurveyUnit(newSU);
			newSU.setStateData(statedat);
		}
	}

	private void updateData(SurveyUnit newSU, JsonNode surveyUnit) {
		JsonNode data = surveyUnit.get("data");
		if(newSU.getData()!=null) {
			newSU.getData().setValue(data);
		} else {
			Data dat = new Data();
			dat.setValue(data);
			dat.setSurveyUnit(newSU);
			newSU.setData(dat);
		}
	}

	private void updateComment(SurveyUnit newSU, JsonNode surveyUnit) {
		JsonNode comment = surveyUnit.get("comment");
		if(newSU.getComment()!=null) {
			newSU.getComment().setValue(comment);
		} else {
			Comment com = new Comment();
			com.setValue(comment);
			com.setSurveyUnit(newSU);
			newSU.setComment(com);
		}
	}

	private void updatePersonalization(SurveyUnit newSU, JsonNode surveyUnit) {
		JsonNode personalization = surveyUnit.get("personalization");
		if(newSU.getPersonalization()!=null) {
			newSU.getPersonalization().setValue(personalization);
		} else {
			Personalization pers = new Personalization();
			pers.setValue(personalization);
			pers.setSurveyUnit(newSU);
			newSU.setPersonalization(pers);
		}
	}

	public void generateDepositProof(SurveyUnit su, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = utilsService.getUserId(request);
		String campaignId = su.getCampaign().getId();
		String campaignLabel = su.getCampaign().getLabel();
		String date = "";
		if(su.getStateData().getState().equals(StateDataType.EXPORTED)) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm"); 
			date = dateFormat.format(new Date(su.getStateData().getDate()));
		}
		ExportPdf exp = new ExportPdf();
		try {
			exp.doExport(response, date, campaignId, campaignLabel, userId);
		} catch (ServletException | IOException e) {
			throw e;
        }
	}
	
	
	public Collection<SurveyUnitResponseDto> getSurveyUnitsByCampaign(String id, HttpServletRequest request) throws BadRequestException{
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("GET survey-units for campaign with id {} resulting in 404", id);
			return Collections.emptyList();
		}
		Map<String, SurveyUnitResponseDto> surveyUnitMap = new HashMap<>();
		ResponseEntity<Object> result = utilsService.getSuFromPilotage(request);
		LOGGER.info("GET survey-units from PearJam API resulting in {}", result.getStatusCode());
		if(result.getStatusCode()!=HttpStatus.OK) {
			LOGGER.error("GET survey-units for campaign with id {} resulting in 500"
					+ "caused by one of following: \n"
					+ "- No survey unit found in pearl jam DB \n"
					+ "- User not authorized ", id);
			throw new BadRequestException(400, "bad request");
		}
		@SuppressWarnings("unchecked")
		List<LinkedHashMap<String,String>> objects = (List<LinkedHashMap<String, String>>) result.getBody();
		if(objects.isEmpty()) {
			LOGGER.info("GET survey-units for campaign with id {} resulting in 404", id);
			return Collections.emptyList();
		}
		LOGGER.info("Number of SU read in Pearl Jam API : {}", objects.size());
		LOGGER.info("Detail : {}", displayDetail(objects));
		for(LinkedHashMap<String, String> map : objects) {
			if(map.get("campaign").equals(id)) {
				LOGGER.info("ID : {}", map.get("id"));
				Optional<SurveyUnit> su = surveyUnitRepository.findById(map.get("id"));
				if(su.isPresent() && surveyUnitMap.get(su.get().getId())==null) {
					LOGGER.info("ID is present");
					surveyUnitMap.put(su.get().getId(), new SurveyUnitResponseDto(su.get().getId(), su.get().getQuestionnaireModelId(), null, null, null, null));
				}
			}
		}
		LOGGER.info("Number of SU to return : {}", surveyUnitMap.size());
		LOGGER.info("GET survey-units for campaign with id {} resulting in 200", id);
		return surveyUnitMap.values();			
	}
	

	private String displayDetail(List<LinkedHashMap<String, String>> objects) {
		Map<String,Integer> nbSUbyCampaign = new HashMap<>();
		for(LinkedHashMap<String, String> map : objects) {
			if(nbSUbyCampaign.get(map.get(Constants.CAMPAIGN))==null) {
				nbSUbyCampaign.put(map.get(Constants.CAMPAIGN), 0);
			}
			nbSUbyCampaign.put(map.get(Constants.CAMPAIGN),  nbSUbyCampaign.get(map.get(Constants.CAMPAIGN))+1);
		}
		return "["+nbSUbyCampaign.entrySet()
	            .stream()
	            .map(entry -> entry.getKey() + ": " + entry.getValue() + " Suvey unit")
	            .collect(Collectors.joining("; "))+"]";

	}

	@Override
	public void createSurveyUnit(SurveyUnitResponseDto su, Campaign campaign, QuestionnaireModel questionnaire) {
		SurveyUnit newSu = new SurveyUnit(su.getId(),campaign,questionnaire,null,null,null,null);
		surveyUnitRepository.save(newSu);
		Data d = new Data(UUID.randomUUID(),Version.INIT,su.getData(),newSu);
		dataService.save(d);
		Comment c = new Comment(UUID.randomUUID(),su.getComment(),newSu);
		commentService.save(c);
		Personalization p = new Personalization(UUID.randomUUID(),su.getPersonalization(),newSu);
		personalizationService.save(p);
		StateDataType type = StateDataType.INIT;
		StateData sd;
		if(su.getStateData() != null) {
			if (su.getStateData().getState() != null ) {
				type = su.getStateData().getState();
			}
			sd = new StateData(UUID.randomUUID(),type,su.getStateData().getDate(),su.getStateData().getCurrentPage(),newSu);
		}
		else {
			sd = new StateData(UUID.randomUUID(),type,null,null,newSu);
		}
		stateDataService.save(sd);
		newSu.setData(d);
		newSu.setStateData(sd);
		newSu.setComment(c);
		newSu.setPersonalization(p);
		surveyUnitRepository.save(newSu);
	}

}
