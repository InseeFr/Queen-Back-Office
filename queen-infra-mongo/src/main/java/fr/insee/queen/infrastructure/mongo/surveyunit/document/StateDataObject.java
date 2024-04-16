package fr.insee.queen.infrastructure.mongo.surveyunit.document;

import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
public class StateDataObject {

    /**
     * The State of the state data
     */
    @Field("state")
    private StateDataType state;

    /**
     * The save date of State
     */
    @Field("date")
    private Long date;

    /**
     * The current page of the StateData
     */
    @Field("current-page")
    private String currentPage;

    public static StateData toModel(@NonNull StateDataObject stateDataObject) {
        return new StateData(stateDataObject.state, stateDataObject.getDate(), stateDataObject.getCurrentPage());
    }

    public static StateDataObject fromModel(StateData stateData) {
        return new StateDataObject(stateData.state(), stateData.date(), stateData.currentPage());
    }
}
