package fr.insee.queen.api.pilotage.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
public class PilotageSurveyUnit {
    private String id;
    private String campaign;
}
