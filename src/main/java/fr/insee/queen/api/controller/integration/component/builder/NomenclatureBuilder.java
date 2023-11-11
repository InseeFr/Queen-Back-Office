package fr.insee.queen.api.controller.integration.component.builder;

import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;

public interface NomenclatureBuilder {
    /**
     * Try to create the nomenclatures
     * @param integrationZipFile zip file containg all infos for integration
     * @return List of {@link IntegrationResultUnitDto} integration results for the nomenclatures
     */
    List<IntegrationResultUnitDto> build(ZipFile integrationZipFile);
}

