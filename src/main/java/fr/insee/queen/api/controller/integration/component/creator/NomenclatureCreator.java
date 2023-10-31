package fr.insee.queen.api.controller.integration.component.creator;

import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.service.questionnaire.NomenclatureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class NomenclatureCreator {
    private final NomenclatureService nomenclatureService;

    public List<IntegrationResultUnitDto> create(List<NomenclatureInputDto> nomenclatures) {
        List<IntegrationResultUnitDto> integrationResults = new ArrayList<>();
        for(NomenclatureInputDto nomenclature : nomenclatures) {
            try {
                create(nomenclature);
                integrationResults.add(IntegrationResultSuccessUnitDto.integrationResultUnitCreated(
                        nomenclature.id(),
                        null));
            } catch (IntegrationValidationException ex) {
                integrationResults.add(ex.resultError());
            }
        }
        return integrationResults;
    }

    private void create(NomenclatureInputDto nomenclature) throws IntegrationValidationException {
        String nomenclatureId = nomenclature.id();
        if(nomenclatureService.existsById(nomenclatureId)) {
            log.info("Nomenclature {} already exists", nomenclatureId);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.NOMENCLATURE_ALREADY_EXISTS, nomenclatureId)));
        }
        log.info("Creating nomenclature {}", nomenclatureId);
        nomenclatureService.saveNomenclature(nomenclature);

    }
}

