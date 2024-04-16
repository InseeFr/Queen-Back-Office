package fr.insee.queen.infrastructure.mongo.questionnaire.dao;

import fr.insee.queen.domain.campaign.gateway.NomenclatureRepository;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.NomenclatureDocument;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import fr.insee.queen.infrastructure.mongo.questionnaire.repository.NomenclatureMongoRepository;
import fr.insee.queen.infrastructure.mongo.questionnaire.repository.QuestionnaireMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NomenclatureMongoDao implements NomenclatureRepository {

    private final NomenclatureMongoRepository repository;
    private final QuestionnaireMongoRepository questionnaireRepository;

    @Override
    public Optional<Nomenclature> find(String nomenclatureId) {
        return repository.findById(nomenclatureId)
                .map(NomenclatureDocument::toModel);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        List<String> nomenclatures = repository.findAllNomenclatureIds()
                .stream()
                .flatMap(Collection::stream)
                .map(NomenclatureDocument::getId)
                .toList();
        return Optional.of(nomenclatures);
    }

    @Override
    public void update(Nomenclature nomenclature) {
        NomenclatureDocument nomenclatureDocument = NomenclatureDocument.fromModel(nomenclature);
        repository.save(nomenclatureDocument);
    }

    @Override
    public void create(Nomenclature nomenclature) {
        NomenclatureDocument nomenclatureDocument = NomenclatureDocument.fromModel(nomenclature);
        repository.insert(nomenclatureDocument);
    }

    @Override
    public List<String> findRequiredNomenclatureByCampaignId(String campaignId) {
        return questionnaireRepository.findRequiredNomenclatureByCampaignId(campaignId)
                .stream()
                .map(QuestionnaireModelDocument::getNomenclatures)
                .flatMap(Collection::stream)
                .map(NomenclatureDocument::getId)
                .distinct()
                .toList();
    }

    @Override
    public List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId) {
        return questionnaireRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireId)
                .stream()
                .map(QuestionnaireModelDocument::getNomenclatures)
                .flatMap(Collection::stream)
                .map(NomenclatureDocument::getId)
                .distinct()
                .toList();
    }

    @Override
    public boolean exists(String nomenclatureId) {
        return repository.existsById(nomenclatureId);
    }
}
