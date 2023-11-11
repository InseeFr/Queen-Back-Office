package fr.insee.queen.api.controller.integration.builder.dummy;

import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.controller.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.Getter;

import java.util.List;
import java.util.zip.ZipFile;

public class QuestionnaireFakeBuilder implements QuestionnaireBuilder {
    @Getter
    private final List<IntegrationResultUnitDto> results = List.of(
            IntegrationResultSuccessUnitDto.integrationResultUnitUpdated("id-questionnaire1"),
            new IntegrationResultErrorUnitDto("id-questionnaire2", IntegrationResultLabel.FILE_INVALID)
    );

    @Override
    public List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile) {
        return results;
    }
}
