package fr.insee.queen.infrastructure.db.configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.data.entity.ciphered.CipheredDataDB;
import fr.insee.queen.infrastructure.db.data.entity.unciphered.UncipheredDataDB;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import lombok.RequiredArgsConstructor;

/**
 * Factory used to provide entities/repository based on the sensitive-data setting
 */
@RequiredArgsConstructor
public class DataFactory {
    private final boolean isCiphered;

    public DataDB buildData(ObjectNode data, InterrogationDB interrogationDB) {
       if(isCiphered) {
            return new CipheredDataDB(data, interrogationDB);
       }
       return new UncipheredDataDB(data, interrogationDB);
    }
}
