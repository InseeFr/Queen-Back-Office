package fr.insee.queen.api.integration.controller.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.web.validation.IdValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NomenclatureIntegrationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotNull
        ArrayNode value) {
    public static Nomenclature toModel(NomenclatureIntegrationData nomenclatureCreationDto) {
        return new Nomenclature(nomenclatureCreationDto.id(), nomenclatureCreationDto.label(), nomenclatureCreationDto.value().toString());
    }
}
