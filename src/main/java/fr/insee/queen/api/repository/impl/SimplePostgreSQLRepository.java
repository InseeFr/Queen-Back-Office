package fr.insee.queen.api.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.repository.SimpleApiRepository;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class SimplePostgreSQLRepository implements SimpleApiRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;



    @Override
    public void updateSurveyUnitData(String id, JsonNode data) {
        updateJsonValueOfSurveyUnit("data",id,data);
    }

    @Override
    public void updateSurveyUnitComment(String id, JsonNode comment)  {
        updateJsonValueOfSurveyUnit("comment",id,comment);
    }

    @Override
    public void updateSurveyUnitPersonalization(String id, JsonNode personalization) {
        updateJsonValueOfSurveyUnit("personalization",id, personalization);
    }

    @Override
    public void updateSurveyUnitStateDate(String id, JsonNode stateData){
        Long date = stateData.get("date").longValue();
        String state = stateData.get("state").textValue();
        String currentPage = stateData.get("currentPage").textValue();

        String qString = "UPDATE state_data SET current_page=?, date=?, state=? WHERE survey_unit_id=?";
        jdbcTemplate.update(qString,currentPage,date,state,id);
    }

    @Override
    public void createSurveyUnit(String campaignId, SurveyUnitResponseDto surveyUnitResponseDto) {
        String su ="INSERT INTO survey_unit (id, campaign_id, questionnaire_model_id)\n" +
                "VALUES (?,?,?)\n" +
                "ON CONFLICT (id) DO UPDATE SET campaign_id=?, questionnaire_model_id=?";
        jdbcTemplate.update(su,
                surveyUnitResponseDto.getId(),
                campaignId, surveyUnitResponseDto.getQuestionnaireId(),
                campaignId, surveyUnitResponseDto.getQuestionnaireId());

        insertJsonValueOfSurveyUnit("data",surveyUnitResponseDto.getId(),surveyUnitResponseDto.getData());
        insertJsonValueOfSurveyUnit("comment",surveyUnitResponseDto.getId(),surveyUnitResponseDto.getComment());
        insertJsonValueOfSurveyUnit("personalization",surveyUnitResponseDto.getId(),surveyUnitResponseDto.getPersonalization());
        insertSurveyUnitStateDate(surveyUnitResponseDto.getId(),surveyUnitResponseDto.getStateData());
    }

    private void insertSurveyUnitStateDate(String surveyUnitId, StateDataDto stateData){
        Long date = stateData.getDate();
        String state = stateData.getState().name();
        String currentPage = stateData.getCurrentPage();
        String qString = "INSERT INTO state_data (id,current_page,date,state,survey_unit_id) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(qString,UUID.randomUUID(),currentPage,date,state,surveyUnitId);
    }

    private void insertJsonValueOfSurveyUnit(String table, String surveyUnitId, JsonNode jsonValue){
        String qString = String.format("INSERT INTO %s (id, value, survey_unit_id) VALUES (?,?,?)",table);
        PGobject json = new PGobject();
        json.setType("json");
        try {
            json.setValue(jsonValue.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jdbcTemplate.update(qString,UUID.randomUUID(),json,surveyUnitId);
    }


    private void updateJsonValueOfSurveyUnit(String table, String surveyUnitId, JsonNode jsonValue) {
        String qString = String.format("UPDATE %s SET value=? WHERE survey_unit_id=?",table);
        PGobject q = new PGobject();
        q.setType("json");
        try {
            q.setValue(jsonValue.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jdbcTemplate.update(qString, q, surveyUnitId);
    }
}
