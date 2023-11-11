package fr.insee.queen.api.campaign.controller.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.web.validation.IdValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NomenclatureCreationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotNull
        ArrayNode value) {
    public static Nomenclature toModel(NomenclatureCreationData nomenclatureCreationDto) {
        return new Nomenclature(nomenclatureCreationDto.id(), nomenclatureCreationDto.label(), nomenclatureCreationDto.value().toString());
    }
}
