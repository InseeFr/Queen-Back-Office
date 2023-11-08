package fr.insee.queen.api.repository.jpa;

import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.repository.entity.CampaignDB;
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
public interface CampaignJpaRepository extends JpaRepository<CampaignDB, String> {
	Optional<CampaignDB> findById(String id);

    @Query("select c from CampaignDB c left join fetch c.metadata left join fetch c.questionnaireModels")
    List<CampaignDB> findAllWithQuestionnaireModels();

    @Query("select c from CampaignDB c left join fetch c.metadata left join fetch c.questionnaireModels where c.id=:campaignId")
    Optional<CampaignDB> findWithQuestionnaireModels(String campaignId);

    @Query("""
		select new fr.insee.queen.api.dto.metadata.MetadataDto(c.metadata.value)
		from CampaignDB c where c.id=:campaignId""")
    Optional<MetadataDto> findMetadataByCampaignId(String campaignId);

    @Query("""
		select new fr.insee.queen.api.dto.metadata.MetadataDto(c.metadata.value)
		from CampaignDB c INNER JOIN c.questionnaireModels qm
		where qm.id=:questionnaireId""")
    Optional<MetadataDto> findMetadataByQuestionnaireId(String questionnaireId);
}
