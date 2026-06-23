package fr.insee.queen.application.integration.controller.builder.dummy;

import fr.insee.queen.application.integration.component.builder.CampaignBuilder;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.zip.ZipFile;

public class CampaignFakeBuilder implements CampaignBuilder {
    @Setter
    private boolean resultIsInErrorState = false;

    @Getter
    private final IntegrationResultUnitDto resultSuccess = IntegrationResultUnitDto.integrationResultUnitCreated("id-campaign");

    @Getter
    private final IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError("id-campaign", "error");

    @Getter
    private Set<String> receivedQuestionnaireIds = Set.of();

    @Override
    public IntegrationResultUnitDto build(ZipFile integrationZipFile, Set<String> questionnaireIds) {
        this.receivedQuestionnaireIds = questionnaireIds;
        return resultIsInErrorState ? resultError : resultSuccess;
    }
}
