package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.campaign.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.repository.entity.CampaignDB;
import fr.insee.queen.api.repository.entity.MetadataDB;
import fr.insee.queen.api.repository.entity.QuestionnaireModelDB;
import fr.insee.queen.api.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.api.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.gateway.CampaignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class CampaignDao implements CampaignRepository {

    private final CampaignJpaRepository campaignJpaRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Transactional
    public void create(CampaignData campaignData) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaignData.questionnaireIds());
        CampaignDB campaign = new CampaignDB(campaignData.id(), campaignData.label(), questionnaireModels);
        questionnaireModels.parallelStream()
                .forEach(questionnaireModel -> questionnaireModel.campaign(campaign));

        String metadataValue = campaignData.metadata();
        if (metadataValue != null) {
            MetadataDB m = new MetadataDB(UUID.randomUUID(), metadataValue, campaign);
            campaign.metadata(m);
        }
        campaignJpaRepository.save(campaign);
    }

    public Boolean exists(String campaignId) {
        return campaignJpaRepository.existsById(campaignId);
    }

    public List<CampaignSummaryDto> getAllWithQuestionnaireIds() {
        return campaignJpaRepository.findAllWithQuestionnaireModels().stream()
                .map(campaign -> new CampaignSummaryDto(
                        campaign.id(),
                        campaign.questionnaireModels().stream().map(QuestionnaireModelDB::id).toList())
                )
                .toList();
    }

    public void delete(String campaignId) {
        campaignJpaRepository.deleteById(campaignId);
    }

    public Optional<CampaignSummaryDto> findWithQuestionnaireIds(String campaignId) {
        Optional<CampaignDB> campaignOpt = campaignJpaRepository.findWithQuestionnaireModels(campaignId);
        if(campaignOpt.isEmpty()) {
            return Optional.empty();
        }
        CampaignDB campaign = campaignOpt.get();
        return Optional.of(new CampaignSummaryDto(
                        campaign.id(),
                        campaign.questionnaireModels().stream().map(QuestionnaireModelDB::id).toList())
                );
    }

    public void update(CampaignData campaignData) {
        CampaignDB campaign = campaignJpaRepository.findById(campaignData.id())
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
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaignData.questionnaireIds());
        campaign.questionnaireModels(questionnaireModels);
        campaignJpaRepository.save(campaign);
    }

    public Optional<MetadataDto> findMetadataByCampaignId(String campaignId) {
        return campaignJpaRepository.findMetadataByCampaignId(campaignId);
    }

    public Optional<MetadataDto> findMetadataByQuestionnaireId(String questionnaireId) {
        return campaignJpaRepository.findMetadataByQuestionnaireId(questionnaireId);
    }
}
