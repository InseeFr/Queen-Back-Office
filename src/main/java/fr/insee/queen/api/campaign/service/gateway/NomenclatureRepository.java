package fr.insee.queen.api.campaign.service.gateway;

import fr.insee.queen.api.campaign.service.model.Nomenclature;

import java.util.List;
import java.util.Optional;

/**
 * NomenclatureRepository is the repository using to access to Nomenclature table in DB
 *
 * @author Claudel Benjamin
 */
public interface NomenclatureRepository {
    Optional<Nomenclature> find(String nomenclatureId);

    Optional<List<String>> findAllIds();

    void update(Nomenclature nomenclature);

    void create(Nomenclature nomenclature);

    List<String> findRequiredNomenclatureByCampaignId(String campaignId);

    List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);

    boolean exists(String nomenclatureId);
}
