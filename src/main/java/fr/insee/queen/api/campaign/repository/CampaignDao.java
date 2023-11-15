package fr.insee.queen.api.campaign.repository;

import fr.insee.queen.api.campaign.repository.entity.CampaignDB;
import fr.insee.queen.api.campaign.repository.entity.MetadataDB;
import fr.insee.queen.api.campaign.repository.entity.QuestionnaireModelDB;
import fr.insee.queen.api.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.api.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.api.campaign.service.gateway.CampaignRepository;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CampaignDao implements CampaignRepository {

    private final CampaignJpaRepository jpaRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelJpaRepository;

    @Override
    @Transactional
    public void create(Campaign campaign) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaign.questionnaireIds());
        CampaignDB campaignDB = new CampaignDB(campaign.id(), campaign.label(), questionnaireModels);
        questionnaireModels.parallelStream()
                .forEach(questionnaireModel -> questionnaireModel.campaign(campaignDB));

        String metadataValue = campaign.metadata();
        if (metadataValue != null) {
            MetadataDB m = new MetadataDB(metadataValue, campaignDB);
            campaignDB.metadata(m);
        }
        jpaRepository.save(campaignDB);
    }

    @Override
    public boolean exists(String campaignId) {
        return jpaRepository.existsById(campaignId);
    }

    @Override
    public List<CampaignSummary> getAllWithQuestionnaireIds() {
        return jpaRepository.findAllWithQuestionnaireModels().stream()
                .map(campaign -> new CampaignSummary(
                        campaign.id(),
                        campaign.label(),
                        campaign.questionnaireModels()
                                .stream()
                                .map(QuestionnaireModelDB::id)
                                .collect(Collectors.toSet()))
                )
                .toList();
    }

    @Override
    public void delete(String campaignId) {
        jpaRepository.deleteById(campaignId);
    }

    @Override
    public Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId) {
        Optional<CampaignDB> campaignOpt = jpaRepository.findWithQuestionnaireModels(campaignId);
        if (campaignOpt.isEmpty()) {
            return Optional.empty();
        }
        CampaignDB campaign = campaignOpt.get();
        return Optional.of(new CampaignSummary(
                campaign.id(),
                campaign.label(),
                campaign.questionnaireModels()
                        .stream()
                        .map(QuestionnaireModelDB::id)
                        .collect(Collectors.toSet()))
        );
    }

    @Override
    public void update(Campaign campaign) {
        CampaignDB campaignDB = jpaRepository.findById(campaign.id())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaign.id())));
        campaignDB.label(campaign.label());

        String metadataValue = campaign.metadata();
        MetadataDB metadata = campaignDB.metadata();
        if (metadata == null) {
            metadata = new MetadataDB(metadataValue, campaignDB);
            campaignDB.metadata(metadata);
        } else {
            metadata.value(metadataValue);
        }
        campaignDB.questionnaireModels().clear();
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaign.questionnaireIds());
        campaignDB.questionnaireModels(questionnaireModels);
        jpaRepository.save(campaignDB);
    }

    @Override
    public Optional<String> findMetadataByCampaignId(String campaignId) {
        return jpaRepository.findMetadataByCampaignId(campaignId);
    }

    @Override
    public Optional<String> findMetadataByQuestionnaireId(String questionnaireId) {
        return jpaRepository.findMetadataByQuestionnaireId(questionnaireId);
    }
}
