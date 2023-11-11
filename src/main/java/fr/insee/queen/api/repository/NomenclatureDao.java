package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.repository.entity.NomenclatureDB;
import fr.insee.queen.api.repository.jpa.NomenclatureJpaRepository;
import fr.insee.queen.api.service.gateway.NomenclatureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class NomenclatureDao implements NomenclatureRepository {

    private final NomenclatureJpaRepository jpaRepository;

    @Override
    public Optional<NomenclatureDto> find(String nomenclatureId) {
        return jpaRepository.findNomenclatureById(nomenclatureId);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return jpaRepository.findAllNomenclatureIds();
    }

    @Override
    public Set<NomenclatureDB> find(Set<String> nomenclatureIds) {
        return jpaRepository.findAllByIdIn(nomenclatureIds);
    }

    @Override
    public void update(String id, String label, String value) {
        jpaRepository.updateNomenclature(id, label, value);
    }

    @Override
    public void create(String id, String label, String value) {
        jpaRepository.createNomenclature(id, label, value);
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
