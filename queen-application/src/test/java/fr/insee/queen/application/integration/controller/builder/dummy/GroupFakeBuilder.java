package fr.insee.queen.application.integration.controller.builder.dummy;

import fr.insee.queen.application.integration.component.builder.GroupBuilder;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

public class GroupFakeBuilder implements GroupBuilder {
    @Setter
    private boolean resultIsInErrorState = false;

    @Getter
    private final List<IntegrationResultUnitDto> resultsSuccess = List.of(IntegrationResultUnitDto.integrationResultUnitCreated("id-campaign"));

    @Getter
    private final List<IntegrationResultUnitDto> resultsError = List.of(IntegrationResultUnitDto.integrationResultUnitError("id-campaign", "error"));

    @Getter
    private Set<String> receivedQuestionnaireIds = Set.of();

    @Override
    public List<IntegrationResultUnitDto> build(ZipFile integrationZipFile, Set<String> questionnaireIds) {
        this.receivedQuestionnaireIds = questionnaireIds;
        return resultIsInErrorState ? resultsError : resultsSuccess;
    }
}
