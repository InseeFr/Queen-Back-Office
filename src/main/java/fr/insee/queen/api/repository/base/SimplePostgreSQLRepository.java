package fr.insee.queen.api.repository.base;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.domain.StateData;
import org.json.simple.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@ConditionalOnProperty(prefix = "fr.insee.queen.application", name = "persistenceType", havingValue = "JPA", matchIfMissing = true)
public class SimplePostgreSQLRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void updateSurveyUnitData(String id, JsonNode data) {
        updateJsonValueOfSurveyUnit("data",id,data);
    }

    public void updateSurveyUnitComment(String id, JsonNode comment)  {
        updateJsonValueOfSurveyUnit("comment",id,comment);
    }

    public void updateSurveyUnitPersonalization(String id, JsonNode personalization) {
        updateJsonValueOfSurveyUnit("personalization",id, personalization);
    }

    public void updateSurveyUnitStateDate(String id, JsonNode stateData){
        Long date = stateData.get("date").longValue();
        String state = stateData.get("state").textValue();
        String currentPage = stateData.get("currentPage").textValue();

        String qString = "UPDATE state_data SET current_page=?, date=?, state=? WHERE survey_unit_id=?";
        jdbcTemplate.update(qString,currentPage,date,state,id);
    }

    private void updateJsonValueOfSurveyUnit(String table, String id, JsonNode jsonValue) {
        String qString = "UPDATE ? SET value=? WHERE survey_unit_id=?";
        PGobject q = new PGobject();
        q.setType("json");
        try {
            q.setValue(jsonValue.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jdbcTemplate.update(qString, table, q, id);
    }
}
