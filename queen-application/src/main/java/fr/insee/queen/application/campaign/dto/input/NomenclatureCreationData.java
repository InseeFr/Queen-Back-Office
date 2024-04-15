package fr.insee.queen.application.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * nomenclature data used to create nomenclature
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "NomenclatureCreation")
public record NomenclatureCreationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotNull
        ArrayNode value) {
    public static Nomenclature toModel(NomenclatureCreationData nomenclatureCreationDto) {
        return new Nomenclature(nomenclatureCreationDto.id(), nomenclatureCreationDto.label(), nomenclatureCreationDto.value());
    }
}
