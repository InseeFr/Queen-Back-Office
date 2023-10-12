package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
* CampaignRepository is the repository using to access to Campaign table in DB
* 
* @author Claudel Benjamin
* 
*/
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
	Optional<Campaign> findById(String id);

    @Query("""
        from Campaign c inner join fetch c.metadata left join fetch c.questionnaireModels where c.id = :campaignId
    """)
    Optional<Campaign> findWithQuestionnairesById(String campaignId);
}
