package fr.insee.queen.api.integration.controller.component.builder;

import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;

import java.util.zip.ZipFile;

public interface CampaignBuilder {
    /**
     * Try to create the campaign
     *
     * @param integrationZipFile zip file containg all infos for integration
     * @return {@link IntegrationResultUnitDto} integration result
     */
    IntegrationResultUnitDto build(ZipFile integrationZipFile);
}
