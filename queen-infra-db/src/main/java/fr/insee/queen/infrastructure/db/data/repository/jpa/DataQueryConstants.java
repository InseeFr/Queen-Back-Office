package fr.insee.queen.infrastructure.db.data.repository.jpa;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataQueryConstants {
    public static final String DELETE_QUERY_FROM_CAMPAIGN_ID = """
            delete from data where survey_unit_id in (
                select id from survey_unit
                    where campaign_id = :campaignId
            )""";

    public static final String DELETE_QUERY_FROM_SURVEYUNIT_ID = """
            delete from data where survey_unit_id = :surveyUnitId
            """;

    public static final String FIND_QUERY = """
            select s.data.value from SurveyUnitDB s
            where s.id=:surveyUnitId
            """;
}