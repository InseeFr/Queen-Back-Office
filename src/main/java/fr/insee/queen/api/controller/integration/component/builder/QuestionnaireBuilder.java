package fr.insee.queen.api.controller.integration.component.builder;

import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;


public interface QuestionnaireBuilder {
    /**
     * Try to create the questionnaires
     * @param integrationZipFile integration zip file
     * @return List of {@link IntegrationResultUnitDto} integration results for the questionnaires
     */
    List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile);
}
