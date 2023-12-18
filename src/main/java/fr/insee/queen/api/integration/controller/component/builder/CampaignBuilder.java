package fr.insee.queen.api.integration.controller.component.builder;

import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;

import java.util.zip.ZipFile;

public interface CampaignBuilder {
    /**
     * Create the campaign
     *
     * @param integrationZipFile zip file containing all infos for integration
     * @param isXmlIntegration Is integration done with xml files
     * @return {@link IntegrationResultUnitDto} integration result
     */
    IntegrationResultUnitDto build(ZipFile integrationZipFile, boolean isXmlIntegration);
}
