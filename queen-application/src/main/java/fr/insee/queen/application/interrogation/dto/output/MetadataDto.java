package fr.insee.queen.application.interrogation.dto.output;

import java.util.List;

public record MetadataDto(
        List<MetadataVariableDto> variables,
        LogoDtos logos,
        QuestionnaireContextDto context,
        String label,
        String objectives
) {
}