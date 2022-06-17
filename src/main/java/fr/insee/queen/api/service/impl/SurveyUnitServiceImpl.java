package fr.insee.queen.api.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.insee.queen.api.domain.*;
import fr.insee.queen.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.exception.BadRequestException;
import fr.insee.queen.api.pdfutils.ExportPdf;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.CommentService;
import fr.insee.queen.api.service.DataService;
import fr.insee.queen.api.service.PersonalizationService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import fr.insee.queen.api.service.StateDataService;
import fr.insee.queen.api.service.SurveyUnitService;
import fr.insee.queen.api.service.UtilsService;

@Service
@Transactional
public class SurveyUnitServiceImpl extends AbstractService<SurveyUnit, String> implements SurveyUnitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitServiceImpl.class);
	
  protected final SurveyUnitRepository surveyUnitRepository;

  protected final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
    
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

	@Autowired(required = false)
	private SimpleApiRepository simpleApiRepository;

	@Autowired
	private QuestionnaireModelService questionnaireModelService;

    @Autowired
    public SurveyUnitServiceImpl(SurveyUnitRepository repository, SurveyUnitTempZoneRepository tempZoneRepository) {
        this.surveyUnitRepository = repository;
        this.surveyUnitTempZoneRepository = tempZoneRepository;
    }

    @Override
    protected JpaRepository<SurveyUnit, String> getRepository() {
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
	public List<SurveyUnit> findAll() {
		return surveyUnitRepository.findAll();
	}

	@Override
	@Transactional
	public void updateSurveyUnit(SurveyUnit newSU, JsonNode surveyUnit) {
			if(surveyUnit.get("personalization") != null) {
				this.updatePersonalization(newSU, surveyUnit);
				personalizationRepository.save(newSU.getPersonalization());
			}
			if(surveyUnit.get("comment") != null) {
				this.updateComment(newSU, surveyUnit);
				commentRepository.save(newSU.getComment());
			}
			if(surveyUnit.get("data") != null) {
				this.updateData(newSU, surveyUnit);
				dataRepository.save(newSU.getData());
			}
			if(surveyUnit.get("stateData") != null) {
				this.updateStateData(newSU, surveyUnit);
				stateDataRepository.save(newSU.getStateData());
			}
			surveyUnitRepository.save(newSU);
		}

	@Override
	public void updateSurveyUnitImproved(String id, JsonNode surveyUnit) {
		if(simpleApiRepository!=null){
			LOGGER.info("Method without hibernate");
			if(surveyUnit.get("personalization") != null) {
				simpleApiRepository.updateSurveyUnitPersonalization(id,surveyUnit.get("personalization"));
			}
			if(surveyUnit.get("comment") != null) {
				simpleApiRepository.updateSurveyUnitComment(id,surveyUnit.get("comment"));
			}
			if(surveyUnit.get("data") != null) {
				simpleApiRepository.updateSurveyUnitData(id,surveyUnit.get("data"));
			}
			if(surveyUnit.get("stateData") != null) {
				simpleApiRepository.updateSurveyUnitStateDate(id, surveyUnit.get("stateData"));
			}
		} else {
			LOGGER.info("Method with hibernate");
			// if sqlRepo is null, use classic method
			Optional<SurveyUnit> su = findById(id);
			updateSurveyUnit(su.get(),surveyUnit);
		}
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
		if(Arrays.asList(StateDataType.EXTRACTED,StateDataType.VALIDATED).contains(su.getStateData().getState())) {
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
		if(objects == null || objects.isEmpty()) {
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
	
	public HttpStatus postSurveyUnit(String id, SurveyUnitResponseDto su) {
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.info("POST survey-unit for campaign with id {} resulting in 404", id);
			return HttpStatus.NOT_FOUND;
		}
		Optional<QuestionnaireModel> questionnaireModelOptional = questionnaireModelService.findById(su.getQuestionnaireId());
		if (!questionnaireModelOptional.isPresent() 
				|| campaignOptional.get().getQuestionnaireModels()
				.stream().filter(q -> q.getId().equals(su.getQuestionnaireId()))
				.collect(Collectors.toList())
				.isEmpty()){
			LOGGER.info("POST survey-unit for campaign with id {} resulting in 404", id);
			return HttpStatus.NOT_FOUND;
		}
		Optional<SurveyUnit> surveyUnit = findById(su.getId());
		if (surveyUnit.isPresent()){
			LOGGER.info("POST survey-unit for campaign with id {} resulting in 400 : Survey-unit {} already exist", id, su.getId());
			return HttpStatus.BAD_REQUEST;
		}
		createSurveyUnit(su, campaignOptional.get(), questionnaireModelOptional.get());
		return HttpStatus.OK;
	}

	@Override
	public ResponseEntity<String> postSurveyUnitImproved(String id, SurveyUnitResponseDto su) {
		if(!simpleApiRepository.idCampaignIsPresent(id)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campaign id not found");
		// TODO check if questionnaire exist
		try{
			simpleApiRepository.createSurveyUnit(id,su);
		}catch (Exception e){
			LOGGER.error("Error is",e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Error when POST survey-unit %s for campaign %s", su.getId(),id));
		}
		return ResponseEntity.status(HttpStatus.OK).body("Survey unit created");
	}

	@Override
	public void createSurveyUnit(SurveyUnitResponseDto su, Campaign campaign, QuestionnaireModel questionnaire) {
		SurveyUnit newSu = new SurveyUnit(su.getId(),campaign,questionnaire,null,null,null,null);
		surveyUnitRepository.save(newSu);
		Data d = new Data(UUID.randomUUID(),su.getData(),newSu);
		dataService.save(d);
		Comment c = new Comment(UUID.randomUUID(),su.getComment(),newSu);
		commentService.save(c);
		Personalization p = new Personalization(UUID.randomUUID(),su.getPersonalization(),newSu);
		personalizationService.save(p);
		StateData sd;
		if(su.getStateData() != null) {
			sd = new StateData();
			if (su.getStateData().getState() != null ) {
				sd.setState(su.getStateData().getState());
			}
			if (su.getStateData().getDate() != null ) {
				sd.setDate(su.getStateData().getDate());
			}
			if (su.getStateData().getCurrentPage() != null ) {
				sd.setCurrentPage(su.getStateData().getCurrentPage());
			}
			sd.setSurveyUnit(newSu);
			newSu.setStateData(sd);
			stateDataService.save(sd);
		}
		
		newSu.setData(d);
		newSu.setComment(c);
		newSu.setPersonalization(p);
		surveyUnitRepository.save(newSu);
	}

	@Override
	public Iterable<SurveyUnit> findByIds(List<String> lstSurveyUnitId) {
		return surveyUnitRepository.findAllById(lstSurveyUnitId);
	}
	
	@Override
	public void delete(SurveyUnit su) {
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(su.getId());
		surveyUnitRepository.delete(su);
	}

	@Override
	public void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit){
    	Long date = new Date().getTime();
		SurveyUnitTempZone surveyUnitTempZoneToSave = new SurveyUnitTempZone(id,userId,date,surveyUnit);
    	surveyUnitTempZoneRepository.save(surveyUnitTempZoneToSave);
	}

	@Override
	public List<SurveyUnitTempZone> getAllSurveyUnitTempZone(){
    	return (List<SurveyUnitTempZone>) surveyUnitTempZoneRepository.findAll();
	}

}
