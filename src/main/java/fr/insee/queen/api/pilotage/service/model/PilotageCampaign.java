package fr.insee.queen.api.pilotage.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PilotageCampaign {
    private String id;
    private List<String> questionnaireIds;
}
