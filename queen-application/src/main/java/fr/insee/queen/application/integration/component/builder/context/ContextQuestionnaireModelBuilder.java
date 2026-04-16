package fr.insee.queen.application.integration.component.builder.context;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.modelefiliere.CollectionInstrumentDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.integration.service.IntegrationService;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import fr.insee.queen.domain.registre.service.RegistreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builder for creating QuestionnaireModel from registre collection instrument data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContextQuestionnaireModelBuilder implements QuestionnaireModelIBuilder {

    private final RegistreService registreService;
    private final IntegrationService integrationService;

    @Override
    public List<IntegrationResultUnitDto> build(CollectionInstrumentDto collectionInstrumentDto) {
        log.debug("Building questionnaire model for collection instrument: {}", collectionInstrumentDto.getUrl());

        // Get data from registre
        CollectionInstrument registreCollectionInstrument = registreService.findCollectionInstrumentByUrl(collectionInstrumentDto.getUrl());

        if (registreCollectionInstrument == null) {
            log.warn("No collection instrument found for URL: {}", collectionInstrumentDto.getUrl());
            return List.of(IntegrationResultUnitDto.fromModel(
                    new IntegrationResult(collectionInstrumentDto.getUrl(), IntegrationStatus.ERROR, "Collection instrument not found in registre")
            ));
        }

        Map<CodeList, ArrayNode> modalitiesByCodeList = registreService.findCodeModalitiesByUrl(collectionInstrumentDto.getCodesListsUrl());
        if (modalitiesByCodeList.isEmpty()) {
            log.error("No code lists found for URL: {}", collectionInstrumentDto.getCodesListsUrl());
            return List.of(IntegrationResultUnitDto.fromModel(
                    new IntegrationResult(collectionInstrumentDto.getUrl(), IntegrationStatus.ERROR, "No code lists found in registre")
            ));
        }

        // Create questionnaire model
        QuestionnaireModel questionnaireModel = toQuestionnaireModel(registreCollectionInstrument, modalitiesByCodeList.keySet());

        // Persist questionnaire model
        List<IntegrationResult> questionnaireModelIntegrationResults = integrationService.create(questionnaireModel);

        return questionnaireModelIntegrationResults.stream()
                .map(IntegrationResultUnitDto::fromModel)
                .toList();
    }

    public QuestionnaireModel toQuestionnaireModel(CollectionInstrument collectionInstrument, Set<CodeList> codeLists) {
        return QuestionnaireModel.createQuestionnaireWithoutCampaign(
                collectionInstrument.id(),
                null,
                collectionInstrument.content(),
                codeLists.stream().map(CodeList::id).collect(Collectors.toSet())
        );
    }
}