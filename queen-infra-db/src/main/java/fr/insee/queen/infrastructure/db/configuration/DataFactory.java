package fr.insee.queen.infrastructure.db.configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.data.entity.ciphered.CipheredDataDB;
import fr.insee.queen.infrastructure.db.surveyunit.data.entity.unciphered.UncipheredDataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.SurveyUnitDB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory used to provide entities/repository based on the cipher setting
 */
@RequiredArgsConstructor
@Slf4j
public class DataFactory {
    private final boolean isCiphered;

    public DataDB buildData(ObjectNode data, SurveyUnitDB surveyUnitDB) {
       if(isCiphered) {
            return new CipheredDataDB(data, surveyUnitDB);
       }
       return new UncipheredDataDB(data, surveyUnitDB);
    }
}
