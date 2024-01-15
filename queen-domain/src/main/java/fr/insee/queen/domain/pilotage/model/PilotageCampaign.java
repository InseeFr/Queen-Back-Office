package fr.insee.queen.domain.pilotage.model;

import java.util.List;


public record PilotageCampaign(String id, List<String> questionnaireIds) {
}
