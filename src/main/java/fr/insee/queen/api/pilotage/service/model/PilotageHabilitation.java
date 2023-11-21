package fr.insee.queen.api.pilotage.service.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = false)
@NoArgsConstructor
public class PilotageHabilitation {
    private boolean habilitated;
}
