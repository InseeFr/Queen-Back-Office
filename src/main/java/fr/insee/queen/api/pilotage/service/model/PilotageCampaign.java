package fr.insee.queen.api.pilotage.service.model;

import java.util.List;


public record PilotageCampaign(String id, List<String> questionnaireIds) {
}
