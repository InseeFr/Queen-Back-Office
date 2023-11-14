package fr.insee.queen.api.integration.controller.component.builder;

import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;

public interface NomenclatureBuilder {
    /**
     * Create the nomenclatures
     *
     * @param integrationZipFile zip file containing all infos for integration
     * @return List of {@link IntegrationResultUnitDto} integration results for the nomenclatures
     */
    List<IntegrationResultUnitDto> build(ZipFile integrationZipFile);
}

