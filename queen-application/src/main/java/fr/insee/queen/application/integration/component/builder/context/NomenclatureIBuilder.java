package fr.insee.queen.application.integration.component.builder.context;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.modelefiliere.CollectionInstrumentDto;

import java.util.List;

/**
 * Interface for building Nomenclature from collection instrument data in the context of registre integration.
 */
public interface NomenclatureIBuilder {
    /**
     * Build nomenclatures from collection instrument DTO.
     *
     * @param collectionInstrumentDto the collection instrument DTO
     * @return List of {@link IntegrationResultUnitDto} integration results for the nomenclatures
     */
    List<IntegrationResultUnitDto> build(CollectionInstrumentDto collectionInstrumentDto);
}