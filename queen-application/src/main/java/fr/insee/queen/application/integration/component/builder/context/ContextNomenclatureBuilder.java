package fr.insee.queen.application.integration.component.builder.context;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.modelefiliere.CollectionInstrumentDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.integration.service.IntegrationService;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.service.RegistreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Builder for creating Nomenclature from registre collection instrument data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContextNomenclatureBuilder implements NomenclatureIBuilder {

    private final RegistreService registreService;
    private final IntegrationService integrationService;

    @Override
    public List<IntegrationResultUnitDto> build(CollectionInstrumentDto collectionInstrumentDto) {
        log.debug("Building nomenclatures for collection instrument: {}", collectionInstrumentDto.getUrl());

        Map<CodeList, ArrayNode> modalitiesByCodeList = registreService.findCodeModalitiesByUrl(collectionInstrumentDto.getCodesListsUrl());
        if (modalitiesByCodeList.isEmpty()) {
            log.error("No code lists found for collection instrument: {}", collectionInstrumentDto.getId());
            return List.of(IntegrationResultUnitDto.fromModel(
                    new IntegrationResult(collectionInstrumentDto.getId(), IntegrationStatus.ERROR, "No code lists found in registre for this collection instrument")
            ));
        }

        // Create nomenclatures
        return createNomenclatures(modalitiesByCodeList);
    }

    private List<IntegrationResultUnitDto> createNomenclatures(Map<CodeList, ArrayNode> modalitiesByCodeList) {
        return modalitiesByCodeList.entrySet().stream()
                .map(entry -> {
                    CodeList codeList = entry.getKey();
                    ArrayNode modalities = entry.getValue();

                    if (modalities == null) {
                        log.error("No modalities found for code list: {}", codeList.id());
                        return IntegrationResultUnitDto.fromModel(
                                new IntegrationResult(codeList.id(), IntegrationStatus.ERROR, "Modalities not found in registre for this codeList")
                        );
                    }

                    Nomenclature nomenclature = toNomenclature(codeList, modalities);
                    IntegrationResult result = integrationService.create(nomenclature);
                    return IntegrationResultUnitDto.fromModel(result);
                })
                .toList();
    }

    private Nomenclature toNomenclature(CodeList codeList, ArrayNode modalities) {
        return new Nomenclature(
                codeList.id(),
                null,
                modalities
        );
    }
}