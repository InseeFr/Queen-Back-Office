package fr.insee.queen.application.surveyunit.dto.output;

import java.util.List;

public record MetadataDto(
        List<MetadataVariableDto> variables,
        LogoDtos logos,
        QuestionnaireContextDto context,
        String label,
        String objectives
) {
}
