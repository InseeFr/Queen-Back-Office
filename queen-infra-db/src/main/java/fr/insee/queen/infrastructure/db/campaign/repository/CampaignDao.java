package fr.insee.queen.infrastructure.db.campaign.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.MetadataDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.QuestionnaireModelJpaRepository;
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
    public Optional<Campaign> findCampaign(String campaignId) {
        Optional<CampaignDB> campaignOpt = jpaRepository.findById(campaignId);
        if (campaignOpt.isEmpty()) {
            return Optional.empty();
        }
        ObjectNode metadata = null;
        CampaignDB campaign = campaignOpt.get();
        if(campaign.getMetadata() != null) {
            metadata = campaign.getMetadata().getValue();
        }

        return Optional.of(new Campaign(
                campaign.getId(),
                campaign.getLabel(),
                campaign.getSensitivity(),
                campaign.getQuestionnaireModels()
                        .stream()
                        .map(QuestionnaireModelDB::getId)
                        .collect(Collectors.toSet()),
                metadata)
        );
    }

    @Override
    @Transactional
    public void create(Campaign campaign) {
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaign.getQuestionnaireIds());
        CampaignDB campaignDB = new CampaignDB(campaign.getId(), campaign.getLabel(), campaign.getSensitivity(), questionnaireModels);
        questionnaireModels.parallelStream()
                .forEach(questionnaireModel -> questionnaireModel.setCampaign(campaignDB));

        ObjectNode metadataValue = campaign.getMetadata();
        if (metadataValue != null) {
            MetadataDB m = new MetadataDB(metadataValue, campaignDB);
            campaignDB.setMetadata(m);
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
                        campaign.getId(),
                        campaign.getLabel(),
                        campaign.getSensitivity(),
                        campaign.getQuestionnaireModels()
                                .stream()
                                .map(QuestionnaireModelDB::getId)
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
        Optional<CampaignDB> campaignOpt = jpaRepository.findById(campaignId);
        if (campaignOpt.isEmpty()) {
            return Optional.empty();
        }
        CampaignDB campaign = campaignOpt.get();
        return Optional.of(new CampaignSummary(
                campaign.getId(),
                campaign.getLabel(),
                campaign.getSensitivity(),
                campaign.getQuestionnaireModels()
                        .stream()
                        .map(QuestionnaireModelDB::getId)
                        .collect(Collectors.toSet()))
        );
    }

    @Override
    @Transactional
    public void update(Campaign campaign) {
        CampaignDB campaignDB = jpaRepository.findById(campaign.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaign.getId())));
        campaignDB.setLabel(campaign.getLabel());
        campaignDB.setSensitivity(campaign.getSensitivity());

        ObjectNode metadataValue = campaign.getMetadata();
        MetadataDB metadata = campaignDB.getMetadata();
        if (metadata == null) {
            metadata = new MetadataDB(metadataValue, campaignDB);
            campaignDB.setMetadata(metadata);
        } else {
            metadata.setValue(metadataValue);
        }
        campaignDB.getQuestionnaireModels().clear();
        Set<QuestionnaireModelDB> questionnaireModels = questionnaireModelJpaRepository.findByIdIn(campaign.getQuestionnaireIds());
        campaignDB.setQuestionnaireModels(questionnaireModels);
        jpaRepository.save(campaignDB);
    }

    @Override
    public Optional<ObjectNode> findMetadataByCampaignId(String campaignId) {
        return jpaRepository.findMetadataByCampaignId(campaignId);
    }

    @Override
    public Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId) {
        return jpaRepository.findMetadataByQuestionnaireId(questionnaireId);
    }
}
