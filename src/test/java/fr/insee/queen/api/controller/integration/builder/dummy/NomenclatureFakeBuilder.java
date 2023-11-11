package fr.insee.queen.api.controller.integration.builder.dummy;

import fr.insee.queen.api.controller.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.Getter;

import java.util.List;
import java.util.zip.ZipFile;

public class NomenclatureFakeBuilder implements NomenclatureBuilder {

    @Getter
    private final List<IntegrationResultUnitDto> results = List.of(
            IntegrationResultSuccessUnitDto.integrationResultUnitUpdated("id-nomenclature1"),
            IntegrationResultSuccessUnitDto.integrationResultUnitCreated("id-nomenclature2")
            );
    @Override
    public List<IntegrationResultUnitDto> build(ZipFile integrationZipFile) {
        return results;
    }
}
