package fr.insee.queen.infrastructure.db.campaign.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository to handle campaigns
 */
@Repository
public interface CampaignJpaRepository extends JpaRepository<CampaignDB, String> {

    /**
     * Retrieve all campaigns
     * @return {@link CampaignDB} all campaigns
     */
    @Query("select c from CampaignDB c left join fetch c.metadata left join fetch c.questionnaireModels")
    List<CampaignDB> findAllWithQuestionnaireModels();

    /**
     * Retrieve campaign by id
     * @return {@link CampaignDB} a campaign
     */
    @Query("select c from CampaignDB c left join fetch c.metadata left join fetch c.questionnaireModels where c.id=:campaignId")
    Optional<CampaignDB> findById(String campaignId);

    /**
     * Retrieve the metadata json value of a campaign
     * @param campaignId campaign id
     * @return {@link ObjectNode} json metadata value
     */
    @Query("""
            select c.metadata.value
            from CampaignDB c where c.id=:campaignId""")
    Optional<ObjectNode> findMetadataByCampaignId(String campaignId);

    /**
     * Retrieve the metadata json value of a campaign byt the questionnaire id
     *
     * @param questionnaireId questionnaire id
     * @return {@link ObjectNode} json metadata value
     */
    @Query("""
            select c.metadata.value
            from CampaignDB c INNER JOIN c.questionnaireModels qm
            where qm.id=:questionnaireId""")
    Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId);
}
