package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;

public interface NomenclatureBuilder {
    /**
     * Create the nomenclatures
     *
     * @param integrationZipFile zip file containing all infos for integration
     * @param isXmlIntegration Is integration done with xml files
     * @return List of {@link IntegrationResultUnitDto} integration results for the nomenclatures
     */
    List<IntegrationResultUnitDto> build(ZipFile integrationZipFile, boolean isXmlIntegration);
}

