package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;

import java.util.Set;
import java.util.zip.ZipFile;

public interface CampaignBuilder {
    /**
     * Create the campaign and attach the given questionnaire models to it.
     *
     * @param integrationZipFile zip file containing all infos for integration
     * @param questionnaireIds   ids of questionnaire models to associate with the campaign
     * @return {@link IntegrationResultUnitDto} integration result
     */
    IntegrationResultUnitDto build(ZipFile integrationZipFile, Set<String> questionnaireIds);
}

