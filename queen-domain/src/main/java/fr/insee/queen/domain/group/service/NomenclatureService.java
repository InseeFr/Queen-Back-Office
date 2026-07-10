package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.model.Nomenclature;

import java.util.List;
import java.util.Set;

public interface NomenclatureService {
    Nomenclature getNomenclature(String id);

    boolean existsById(String id);

    boolean areNomenclaturesValid(Set<String> nomenclatureIds);

    void saveNomenclature(Nomenclature nomenclature);

    List<String> getAllNomenclatureIds();

    List<String> findRequiredNomenclatureByGroup(String groupId);

    List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId);
}
