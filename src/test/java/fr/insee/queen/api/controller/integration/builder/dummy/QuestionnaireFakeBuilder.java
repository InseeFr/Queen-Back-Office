package fr.insee.queen.api.controller.integration.builder.dummy;

import fr.insee.queen.api.integration.controller.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
import lombok.Getter;

import java.util.List;
import java.util.zip.ZipFile;

public class QuestionnaireFakeBuilder implements QuestionnaireBuilder {
    @Getter
    private final List<IntegrationResultUnitDto> results = List.of(
            IntegrationResultUnitDto.integrationResultUnitUpdated("id-questionnaire1"),
            IntegrationResultUnitDto.integrationResultUnitError("id-questionnaire2", IntegrationResultLabel.FILE_INVALID)
    );

    @Override
    public List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile) {
        return results;
    }
}
