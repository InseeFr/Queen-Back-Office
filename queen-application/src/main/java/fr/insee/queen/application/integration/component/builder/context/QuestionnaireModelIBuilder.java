package fr.insee.queen.application.integration.component.builder.context;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.modelefiliere.CollectionInstrumentDto;

import java.util.List;

/**
 * Interface for building QuestionnaireModel from collection instrument data.
 */
public interface QuestionnaireModelIBuilder {
    /**
     * Build questionnaire models from collection instrument DTO.
     *
     * @param collectionInstrumentDto the collection instrument DTO
     * @return List of {@link IntegrationResultUnitDto} integration results for the questionnaire models
     */
    List<IntegrationResultUnitDto> build(CollectionInstrumentDto collectionInstrumentDto);
}