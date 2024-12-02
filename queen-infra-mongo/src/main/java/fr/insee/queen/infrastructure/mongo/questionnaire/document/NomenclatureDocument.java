package fr.insee.queen.infrastructure.mongo.questionnaire.document;

import fr.insee.queen.domain.campaign.model.Nomenclature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(value="nomenclature")
public class NomenclatureDocument {
    /**
     * nomenclature id
     */
    @Id
    private String id;
    /**
     * nomenclature label
     */
    @Field("label")
    private String label;

    /**
     * nomenclature value (json)
     */
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private NomenclatureDataObject data;

    private NomenclatureDocument(String id) {
        this.id = id;
    }

    public static NomenclatureDocument fromModel(Nomenclature nomenclature) {
        return new NomenclatureDocument(nomenclature.id(), nomenclature.label(), NomenclatureDataObject.fromModel(nomenclature.value()));
    }

    public static Set<NomenclatureDocument> fromModel(Set<String> nomenclaturesId) {
        return nomenclaturesId.stream()
                .map(NomenclatureDocument::new)
                .collect(Collectors.toSet());
    }

    public static Nomenclature toModel(NomenclatureDocument nomenclatureDocument) {
        return new Nomenclature(nomenclatureDocument.getId(),
                nomenclatureDocument.getLabel(),
                NomenclatureDataObject.toModel(nomenclatureDocument.getData()));
    }
}
