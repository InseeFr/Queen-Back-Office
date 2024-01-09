package fr.insee.queen.infrastructure.db.campaign.repository;

import fr.insee.queen.domain.campaign.gateway.NomenclatureRepository;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.NomenclatureJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class NomenclatureDao implements NomenclatureRepository {

    private final NomenclatureJpaRepository jpaRepository;

    @Override
    public Optional<Nomenclature> find(String nomenclatureId) {
        return jpaRepository.findNomenclatureById(nomenclatureId);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return jpaRepository.findAllNomenclatureIds();
    }

    @Override
    public void update(Nomenclature nomenclature) {
        jpaRepository.updateNomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value());
    }

    @Override
    public void create(Nomenclature nomenclature) {
        jpaRepository.createNomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value());
    }

    @Override
    public List<String> findRequiredNomenclatureByCampaignId(String campaignId) {
        return jpaRepository.findRequiredNomenclatureByCampaignId(campaignId);
    }

    @Override
    public List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId) {
        return jpaRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireId);
    }

    @Override
    public boolean exists(String nomenclatureId) {
        return jpaRepository.existsById(nomenclatureId);
    }
}
