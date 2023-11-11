package fr.insee.queen.api.controller.integration.builder.dummy;

import fr.insee.queen.api.controller.integration.component.builder.CampaignBuilder;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.Getter;
import lombok.Setter;

import java.util.zip.ZipFile;

public class CampaignFakeBuilder implements CampaignBuilder {
    @Setter
    private boolean resultIsInErrorState = false;

    @Getter
    private final IntegrationResultUnitDto resultSuccess = IntegrationResultSuccessUnitDto.integrationResultUnitCreated("id-campaign");

    @Getter
    private final IntegrationResultUnitDto resultError = new IntegrationResultErrorUnitDto("id-campaign", "error");

    @Override
    public IntegrationResultUnitDto build(ZipFile integrationZipFile) {
        return resultIsInErrorState ? resultError : resultSuccess;
    }
}
