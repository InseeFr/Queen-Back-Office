package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.entity.CampaignDB;
import fr.insee.queen.api.entity.QuestionnaireModelDB;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class CampaignRepository {

    private final CampaignCrudRepository campaignCrudRepository;
    private final QuestionnaireModelCrudRepository questionnaireModelCrudRepository;

    @Transactional
    public void createCampaign(String campaignId, String label, List<String>questionnaireIds, String metadataValue) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelCrudRepository.findByIdIn(questionnaireIds);
        CampaignDB campaign = new CampaignDB(campaignId, label, metadataValue, questionnaireModels);
        questionnaireModels.parallelStream()
                .forEach(questionnaireModel -> questionnaireModel.campaign(campaign));
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

    public void updateCampaign(String id, String label, List<String> questionnaireIds, String metadataValue) {
        CampaignDB campaign = campaignCrudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", id)));
        campaign.label(label);
        campaign.metadata(metadataValue);
        campaign.questionnaireModels().clear();
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelCrudRepository.findByIdIn(questionnaireIds);
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
