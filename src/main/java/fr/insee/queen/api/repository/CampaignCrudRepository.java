package fr.insee.queen.api.repository;

import fr.insee.queen.api.entity.CampaignDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
* CampaignRepository is the repository using to access to Campaign table in DB
* 
* @author Claudel Benjamin
* 
*/
@Repository
public interface CampaignCrudRepository extends JpaRepository<CampaignDB, String> {
	Optional<CampaignDB> findById(String id);

    @Query("select c from CampaignDB c left join fetch c.questionnaireModels left join fetch c.metadata")
    List<CampaignDB> findAllWithQuestionnaireModels();

    @Query("select c from CampaignDB c left join fetch c.questionnaireModels left join fetch c.metadata where c.id=:campaignId")
    Optional<CampaignDB> findWithQuestionnaireModels(String campaignId);
}
