package fr.insee.queen.api.repository;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitInputDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitCreateUpdateRepository {
    private final JdbcTemplate jdbcTemplate;

    public void updateSurveyUnitData(String surveyUnitId, JsonNode data) {
        updateValueOfSurveyUnit("data", surveyUnitId, data.toString());
    }

    public void updateSurveyUnitComment(String surveyUnitId, JsonNode comment)  {
        updateValueOfSurveyUnit("comment", surveyUnitId, comment.toString());
    }

    public void updateSurveyUnitPersonalization(String surveyUnitId, JsonNode personalization) {
        updateValueOfSurveyUnit("personalization", surveyUnitId, personalization.toString());
    }

    public void updateSurveyUnitStateData(String surveyUnitId, StateDataInputDto stateData){
        String qStringGetSU = "SELECT count(*) FROM state_data WHERE survey_unit_id=?";
        Long date = stateData.date();
        String state = stateData.state().name();
        String currentPage = stateData.currentPage();

        Integer nbStateData = jdbcTemplate.queryForObject(
                qStringGetSU, Integer.class, surveyUnitId);

        if(nbStateData != null && nbStateData>0) {
            String qString = "UPDATE state_data SET current_page=?, date=?, state=? WHERE survey_unit_id=?";
            jdbcTemplate.update(qString, currentPage, date, state, surveyUnitId);
            return;
        }

        log.info("INSERT state_data for reporting unit with id {}", surveyUnitId);
        String qString = "INSERT INTO state_data (id, current_page, date, state, survey_unit_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(qString, UUID.randomUUID(), currentPage, date, state, surveyUnitId);
    }

    public void createSurveyUnit(String campaignId, SurveyUnitInputDto surveyUnitDto) {
        String su ="""
                INSERT INTO survey_unit (id, campaign_id, questionnaire_model_id)
                VALUES (?,?,?)
                ON CONFLICT (id) DO UPDATE SET campaign_id=?, questionnaire_model_id=?
                """;
        jdbcTemplate.update(su,
                surveyUnitDto.id(),
                campaignId, surveyUnitDto.questionnaireId(),
                campaignId, surveyUnitDto.questionnaireId());

        insertValueOfSurveyUnit("data", surveyUnitDto.id(), surveyUnitDto.data().toString());
        insertValueOfSurveyUnit("comment", surveyUnitDto.id(), surveyUnitDto.comment().toString());
        insertValueOfSurveyUnit("personalization", surveyUnitDto.id(), surveyUnitDto.personalization().toString());
        insertSurveyUnitStateDate(surveyUnitDto.id(), surveyUnitDto.stateData());
    }

    public void deleteParadataEventsBySU(List<String> lstSU) {
        String values = lstSU.stream().map(id->"?").collect(Collectors.joining(","));
        String qStringBuilder = "DELETE FROM paradata_event AS paradataEvent " +
                "WHERE paradataEvent.value ->> 'idSU' IN (%s)";
        String qString = String.format(qStringBuilder, values);
        jdbcTemplate.update(qString, lstSU.toArray());
    }

    private void insertSurveyUnitStateDate(String surveyUnitId, StateDataInputDto stateData){
        Long date = null;
        String state = null;
        String currentPage = null;
        if(stateData != null) {
            date = stateData.date();
            state = stateData.state() != null ? stateData.state().name() : null;
            currentPage = stateData.currentPage();
        }

        String qString = "INSERT INTO state_data (id,current_page,date,state,survey_unit_id) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(qString,UUID.randomUUID(),currentPage,date,state,surveyUnitId);
    }

    private void insertValueOfSurveyUnit(String table, String surveyUnitId, String value){
        String qString = String.format("INSERT INTO %s (id, value, survey_unit_id) VALUES (?,?,?)",table);
        PGobject json = new PGobject();
        json.setType("json");
        try {
            json.setValue(value);
        } catch (SQLException throwables) {
            log.error("Error when inserting in {} - {}",table,throwables.getMessage());
            throwables.printStackTrace();
        }
        jdbcTemplate.update(qString,UUID.randomUUID(),json,surveyUnitId);
    }


    private void updateValueOfSurveyUnit(String table, String surveyUnitId, String value) {
        String qString = String.format("UPDATE %s SET value=? WHERE survey_unit_id=?",table);
        PGobject q = new PGobject();
        q.setType("json");
        try {
            q.setValue(value);
        } catch (SQLException throwables) {
            log.error("Error when updating in {} - {}",table,throwables.getMessage());
            throwables.printStackTrace();
        }
        jdbcTemplate.update(qString, q, surveyUnitId);
    }
}

