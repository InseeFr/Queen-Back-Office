package fr.insee.queen.domain.campaign.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.infrastructure.dummy.CampaignFakeRepository;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetadataServiceTest {
    private MetadataService metadataService;
    private CampaignFakeRepository campaignRepository;
    private ObjectNode metadata;

    @BeforeEach
    void init() {
        metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("field", "value");
        campaignRepository = new CampaignFakeRepository();
        metadataService = new MetadataApiService(campaignRepository);
    }

    @Test
    @DisplayName("When retrieving metadata for questionnaire, throws exception if metadata not found")
    void testMetadataQuestionnaire01() {
        campaignRepository.setMetadata(null);
        assertThatThrownBy(() -> metadataService.getMetadataByQuestionnaireId(CampaignFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When retrieving metadata for questionnaire, return metadata")
    void testMetadataQuestionnaire02() {
        campaignRepository.setMetadata(metadata);
        assertThat(metadataService.getMetadataByQuestionnaireId(CampaignFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isEqualTo(metadata);
    }

    @Test
    @DisplayName("When retrieving metadata for campaign, throws exception if metadata not found")
    void testMetadataCampaign01() {
        campaignRepository.setMetadata(null);
        assertThatThrownBy(() -> metadataService.getMetadata(CampaignFakeRepository.CAMPAIGN_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When retrieving metadata for campaign, return metadata")
    void testMetadataCampaign02() {
        campaignRepository.setMetadata(metadata);
        assertThat(metadataService.getMetadata(CampaignFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isEqualTo(metadata);
    }
}
