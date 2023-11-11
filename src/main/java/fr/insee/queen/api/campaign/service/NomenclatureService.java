package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.model.Nomenclature;

import java.util.List;
import java.util.Set;

public interface NomenclatureService {
    Nomenclature getNomenclature(String id);

    boolean existsById(String id);

    boolean areNomenclaturesValid(Set<String> nomenclatureIds);

    void saveNomenclature(Nomenclature nomenclature);

    List<String> getAllNomenclatureIds();

    List<String> findRequiredNomenclatureByCampaign(String campaignId);

    List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId);
}
