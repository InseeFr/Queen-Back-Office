package fr.insee.queen.api.integration.controller.component.builder;

import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;


public interface QuestionnaireBuilder {
    /**
     * Create questionnaires
     *
     * @param integrationZipFile integration zip file
     * @return List of {@link IntegrationResultUnitDto} integration results for the questionnaires
     */
    List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile);
}
