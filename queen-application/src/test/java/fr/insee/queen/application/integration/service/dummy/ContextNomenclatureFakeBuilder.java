package fr.insee.queen.application.integration.service.dummy;

import fr.insee.queen.application.integration.component.builder.context.NomenclatureIBuilder;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.modelefiliere.CollectionInstrumentDto;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake implementation of NomenclatureIBuilder for testing purposes.
 * Allows configuring behavior to simulate success or failure scenarios.
 */
public class ContextNomenclatureFakeBuilder implements NomenclatureIBuilder {

    @Setter
    private boolean shouldFail = false;

    @Setter
    private String errorMessage = "Failed to create nomenclature";

    @Setter
    private List<IntegrationResultUnitDto> resultsToReturn = new ArrayList<>();

    @Override
    public List<IntegrationResultUnitDto> build(CollectionInstrumentDto collectionInstrumentDto) {
        if (shouldFail) {
            return List.of(IntegrationResultUnitDto.integrationResultUnitError(
                    collectionInstrumentDto.getId(),
                    errorMessage
            ));
        }

        if (!resultsToReturn.isEmpty()) {
            return new ArrayList<>(resultsToReturn);
        }

        // Default success behavior - return a successful result
        return List.of(IntegrationResultUnitDto.integrationResultUnitCreated(
                collectionInstrumentDto.getId()
        ));
    }

    /**
     * Configure the builder to return specific results
     * @param results List of results to return
     */
    public void setResults(List<IntegrationResultUnitDto> results) {
        this.resultsToReturn = new ArrayList<>(results);
    }

    /**
     * Reset the builder to default state
     */
    public void reset() {
        shouldFail = false;
        errorMessage = "Failed to create nomenclature";
        resultsToReturn = new ArrayList<>();
    }
}