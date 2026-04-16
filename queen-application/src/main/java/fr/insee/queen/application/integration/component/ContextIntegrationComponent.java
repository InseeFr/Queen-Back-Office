package fr.insee.queen.application.integration.component;

import fr.insee.modelefiliere.CollectionInstrumentDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.queen.application.integration.component.builder.context.NomenclatureIBuilder;
import fr.insee.queen.application.integration.component.builder.context.QuestionnaireModelIBuilder;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import fr.insee.queen.application.integration.component.builder.context.RegistreUrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for handling context integration logic.
 * Encapsulates the business logic for processing context DTOs and integrating with registre.
 * Uses builders to delegate the creation of questionnaire models and nomenclatures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContextIntegrationComponent {

    private final QuestionnaireModelIBuilder questionnaireModelBuilder;
    private final NomenclatureIBuilder nomenclatureBuilder;
    private final RegistreUrlValidator registreUrlValidator;

    /**
     * Processes context DTO and integrates collection instruments
     *
     * @param contextDto the context DTO containing collection instruments
     * @return integration results
     */
    public IntegrationResultsDto processContext(ContextDto contextDto) {
        log.info("Processing context: {}", contextDto.getId());
        IntegrationResultsDto integrationResults = new IntegrationResultsDto();
        List<IntegrationResultUnitDto> questionnaireModelResults = new ArrayList<>();
        List<IntegrationResultUnitDto> nomenclatureResults = new ArrayList<>();

        for (CollectionInstrumentDto collectionInstrumentDto : contextDto.getCollectionInstruments()) {
            // Validate URLs before processing
            String url = collectionInstrumentDto.getUrl();
            String codesListsUrl = collectionInstrumentDto.getCodesListsUrl();

            if (!registreUrlValidator.isValidUrl(url)) {
                log.error("URL invalide dans CollectionInstrumentDto: {}", url);
                throw new IllegalArgumentException("URL invalide: " + url);
            }

            if (!registreUrlValidator.isValidUrl(codesListsUrl)) {
                log.error("CodesListsUrl invalide dans CollectionInstrumentDto: {}", codesListsUrl);
                throw new IllegalArgumentException("CodesListsUrl invalide: " + codesListsUrl);
            }

            // Use builders to create questionnaire models and nomenclatures
            List<IntegrationResultUnitDto> nomResults = nomenclatureBuilder.build(collectionInstrumentDto);
            List<IntegrationResultUnitDto> qmResults = questionnaireModelBuilder.build(collectionInstrumentDto);

            questionnaireModelResults.addAll(qmResults);
            nomenclatureResults.addAll(nomResults);
        }

        integrationResults.setNomenclatures(nomenclatureResults);
        integrationResults.setQuestionnaireModels(questionnaireModelResults);
        return integrationResults;
    }
}