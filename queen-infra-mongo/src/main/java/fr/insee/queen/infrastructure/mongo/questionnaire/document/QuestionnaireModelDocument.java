package fr.insee.queen.infrastructure.mongo.questionnaire.document;

import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(value="questionnaire-model")
public class QuestionnaireModelDocument {
    /**
     * questionnaire id
     */
    @Id
    private String id;

    /**
     * questionnaire label
     */
    @Field("label")
    private String label;

    @Field("campaign")
    private CampaignObject campaign;

    /**
     * the data structure of the questionnaire (json format)
     */

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private QuestionnaireModelDataObject data;

    /**
     * required nomenclatures for the questionnaire
     */
    @DocumentReference(lazy = true)
    @Field("nomenclatures")
    private Set<NomenclatureDocument> nomenclatures = new HashSet<>();

    private QuestionnaireModelDocument(String id) {
        this.id = id;
    }

    private QuestionnaireModelDocument(String id, String label,
                                      QuestionnaireModelDataObject data,
                                      Set<NomenclatureDocument> nomenclatures) {
        this.id = id;
        this.label = label;
        this.data = data;
        this.nomenclatures = nomenclatures;
    }

    public static QuestionnaireModelDocument fromModel(@NonNull QuestionnaireModel questionnaire) {
        if(questionnaire.getCampaignId() != null) {
            return new QuestionnaireModelDocument(questionnaire.getId(),
                    questionnaire.getLabel(),
                    CampaignObject.fromModel(questionnaire.getCampaignId()),
                    QuestionnaireModelDataObject.fromModel(questionnaire.getValue()),
                    NomenclatureDocument.fromModel(questionnaire.getRequiredNomenclatureIds()));
        }
        return new QuestionnaireModelDocument(questionnaire.getId(),
                questionnaire.getLabel(),
                QuestionnaireModelDataObject.fromModel(questionnaire.getValue()),
                NomenclatureDocument.fromModel(questionnaire.getRequiredNomenclatureIds()));
    }

    public static Set<QuestionnaireModelDocument> fromModel(@NonNull Set<String> questionnairesId) {
        return questionnairesId.stream()
                .map(QuestionnaireModelDocument::new)
                .collect(Collectors.toSet());
    }
}
