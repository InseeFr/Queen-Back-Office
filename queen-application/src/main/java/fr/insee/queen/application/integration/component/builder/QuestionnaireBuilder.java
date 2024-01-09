package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;


public interface QuestionnaireBuilder {
    /**
     * Create questionnaires
     *
     * @param integrationZipFile integration zip file
     * @param isXmlIntegration Is integration done with xml files
     * @return List of {@link IntegrationResultUnitDto} integration results for the questionnaires
     */
    List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile, boolean isXmlIntegration);
}
