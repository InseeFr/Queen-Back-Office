package fr.insee.queen.api.controller.integration.component.builder;

import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;

public interface NomenclatureBuilder {
    List<IntegrationResultUnitDto> build(ZipFile zf);
}

