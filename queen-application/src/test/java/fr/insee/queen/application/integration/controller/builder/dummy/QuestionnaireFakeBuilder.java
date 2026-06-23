package fr.insee.queen.application.integration.controller.builder.dummy;

import fr.insee.queen.application.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import lombok.Setter;

import java.util.List;
import java.util.zip.ZipFile;

public class QuestionnaireFakeBuilder implements QuestionnaireBuilder {
    private static final List<IntegrationResultUnitDto> SUCCESS_RESULTS = List.of(
            IntegrationResultUnitDto.integrationResultUnitUpdated("id-questionnaire1"),
            IntegrationResultUnitDto.integrationResultUnitCreated("id-questionnaire2")
    );

    private static final List<IntegrationResultUnitDto> MIXED_RESULTS = List.of(
            IntegrationResultUnitDto.integrationResultUnitUpdated("id-questionnaire1"),
            IntegrationResultUnitDto.integrationResultUnitError("id-questionnaire2", IntegrationResultLabel.FILE_INVALID)
    );

    @Setter
    private boolean oneResultInErrorState = false;

    public List<IntegrationResultUnitDto> getResults() {
        return oneResultInErrorState ? MIXED_RESULTS : SUCCESS_RESULTS;
    }

    @Override
    public List<IntegrationResultUnitDto> build(ZipFile integrationZipFile) {
        return getResults();
    }
}
