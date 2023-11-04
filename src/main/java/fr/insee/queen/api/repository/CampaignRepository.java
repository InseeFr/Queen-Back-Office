package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.entity.CampaignDB;
import fr.insee.queen.api.entity.MetadataDB;
import fr.insee.queen.api.entity.QuestionnaireModelDB;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class CampaignRepository {

    private final CampaignCrudRepository campaignCrudRepository;
    private final QuestionnaireModelCrudRepository questionnaireModelCrudRepository;

    @Transactional
    public void createCampaign(CampaignData campaignData) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelCrudRepository.findByIdIn(campaignData.questionnaireIds());
        CampaignDB campaign = new CampaignDB(campaignData.id(), campaignData.label(), questionnaireModels);
        questionnaireModels.parallelStream()
                .forEach(questionnaireModel -> questionnaireModel.campaign(campaign));

        String metadataValue = campaignData.metadata();
        if (metadataValue != null) {
            MetadataDB m = new MetadataDB(UUID.randomUUID(), metadataValue, campaign);
            campaign.metadata(m);
        }
        campaignCrudRepository.save(campaign);
    }

    public Boolean existsById(String campaignId) {
        return campaignCrudRepository.existsById(campaignId);
    }

    public List<CampaignSummaryDto> findAllWithQuestionnaireIds() {
        return campaignCrudRepository.findAllWithQuestionnaireModels().stream()
                .map(campaign -> new CampaignSummaryDto(
                        campaign.id(),
                        campaign.questionnaireModels().stream().map(QuestionnaireModelDB::id).toList())
                )
                .toList();
    }

    public void deleteById(String campaignId) {
        campaignCrudRepository.deleteById(campaignId);
    }

    public Optional<CampaignSummaryDto> findWithQuestionnaireIds(String campaignId) {
        Optional<CampaignDB> campaignOpt = campaignCrudRepository.findWithQuestionnaireModels(campaignId);
        if(campaignOpt.isEmpty()) {
            return Optional.empty();
        }
        CampaignDB campaign = campaignOpt.get();
        return Optional.of(new CampaignSummaryDto(
                        campaign.id(),
                        campaign.questionnaireModels().stream().map(QuestionnaireModelDB::id).toList())
                );
    }

    public void updateCampaign(CampaignData campaignData) {
        CampaignDB campaign = campaignCrudRepository.findById(campaignData.id())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaignData.id())));
        campaign.label(campaignData.label());

        String metadataValue = campaignData.metadata();
        MetadataDB metadata = campaign.metadata();
        if(metadata == null) {
            metadata = new MetadataDB(UUID.randomUUID(), metadataValue, campaign);
            campaign.metadata(metadata);
        } else {
            metadata.value(metadataValue);
        }
        campaign.questionnaireModels().clear();
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelCrudRepository.findByIdIn(campaignData.questionnaireIds());
        campaign.questionnaireModels(questionnaireModels);
        campaignCrudRepository.save(campaign);
    }

    public Optional<MetadataDto> findMetadataByCampaignId(String campaignId) {
        return campaignCrudRepository.findMetadataByCampaignId(campaignId);
    }

    public Optional<MetadataDto> findMetadataByQuestionnaireId(String questionnaireId) {
        return campaignCrudRepository.findMetadataByQuestionnaireId(questionnaireId);
    }
}
