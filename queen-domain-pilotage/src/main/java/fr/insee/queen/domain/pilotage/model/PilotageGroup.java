package fr.insee.queen.domain.pilotage.model;

import java.util.List;


public record PilotageGroup(String id, List<String> questionnaireIds) {
}
