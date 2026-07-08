package fr.insee.queen.domain.group.gateway;

import fr.insee.queen.domain.group.model.Nomenclature;

import java.util.List;
import java.util.Optional;

/**
 * Repository to handle nomenclatures
 */
public interface NomenclatureRepository {
    /**
     * Find nomenclature
     * @param nomenclatureId nomenclature id
     * @return {@link Nomenclature} nomenclature
     */
    Optional<Nomenclature> find(String nomenclatureId);

    /**
     * Find all nomenclature ids
     *
     * @return ids
     */
    Optional<List<String>> findAllIds();

    /**
     * Update nomenclature
     *
     * @param nomenclature to update
     */
    void update(Nomenclature nomenclature);

    /**
     * Create nomenclature
     *
     * @param nomenclature to create
     */
    void create(Nomenclature nomenclature);

    /**
     * Find nomenclatures used in group
     * @param groupId group id
     * @return List of required nomenclatures for the group
     */
    List<String> findRequiredNomenclatureByGroupId(String groupId);

    /**
     * Find nomenclatures used in questionnaire
     * @param questionnaireId questionnaire id
     * @return List of required nomenclatures for the questionnaire
     */
    List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);

    /**
     * Check if nomenclature exists
     *
     * @param nomenclatureId nomenclature id
     * @return true if exists, false otherwise
     */
    boolean exists(String nomenclatureId);
}
