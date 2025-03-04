package fr.insee.queen.domain.integration.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.campaign.service.dummy.*;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationServiceTest {
    private IntegrationService integrationService;
    private CampaignExistenceFakeService campaignExistenceService;
    private CampaignFakeService campaignService;
    private QuestionnaireModelExistenceFakeService questionnaireExistenceService;
    private QuestionnaireModelFakeService questionnaireService;
    private NomenclatureFakeService nomenclatureService;
    
    private static final String LABEL = "label";
    private static final String QUESTIONNAIRE_ID = "id-questionnaire";
    private static final String CAMPAIGN_ID = "id-campaign";

    @BeforeEach
    void init() {
        campaignExistenceService = new CampaignExistenceFakeService();
        campaignService = new CampaignFakeService();
        questionnaireExistenceService = new QuestionnaireModelExistenceFakeService();
        questionnaireService = new QuestionnaireModelFakeService();
        nomenclatureService = new NomenclatureFakeService();
        integrationService = new IntegrationApiService(campaignService, campaignExistenceService, questionnaireExistenceService,
                questionnaireService, nomenclatureService);
    }

    @Test
    @DisplayName("On create nomenclature, when nomenclature exists, return integration error")
    void testIntegrationNomenclature01() {
        String nomenclatureId = "id";
        Nomenclature nomenclature = new Nomenclature(nomenclatureId, LABEL, JsonNodeFactory.instance.arrayNode());
        IntegrationResult result = integrationService.create(nomenclature);
        assertThat(result.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.getId()).isEqualTo(nomenclatureId);
        assertThat(result.getCause()).isEqualTo(String.format(IntegrationResultLabel.NOMENCLATURE_ALREADY_EXISTS, nomenclatureId));
        assertThat(nomenclatureService.isSaved()).isFalse();
    }

    @Test
    @DisplayName("On create nomenclature, when nomenclature does not exist, return integration success")
    void testIntegrationNomenclature02() {
        String nomenclatureId = "id";
        nomenclatureService.setNonExistingNomenclatures(List.of(nomenclatureId));
        Nomenclature nomenclature = new Nomenclature(nomenclatureId, LABEL, JsonNodeFactory.instance.arrayNode());
        IntegrationResult result = integrationService.create(nomenclature);
        assertThat(result.getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(result.getId()).isEqualTo(nomenclatureId);
        assertThat(nomenclatureService.isSaved()).isTrue();
    }

    @Test
    @DisplayName("On save campaign, when campaign exists, update campaign")
    void testIntegrationCampaign01() {
        campaignExistenceService.setCampaignExist(true);

        Campaign campaign = new Campaign(CAMPAIGN_ID, LABEL, CampaignSensitivity.NORMAL, JsonNodeFactory.instance.objectNode());
        IntegrationResult campaignResult = integrationService.create(campaign);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.UPDATED);
        assertThat(campaignResult.getId()).isEqualTo(CAMPAIGN_ID);
        assertThat(campaignService.isUpdated()).isTrue();
    }

    @Test
    @DisplayName("On save campaign, when campaign does not exist, create campaign")
    void testIntegrationCampaign02() {
        campaignExistenceService.setCampaignExist(false);

        Campaign campaign = new Campaign(CAMPAIGN_ID, LABEL, CampaignSensitivity.NORMAL, JsonNodeFactory.instance.objectNode());
        IntegrationResult campaignResult = integrationService.create(campaign);

        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(campaignResult.getId()).isEqualTo(CAMPAIGN_ID);
        assertThat(campaignService.isCreated()).isTrue();
    }

    @Test
    @DisplayName("On save questionnaire, when campaign does not exist, return integration error")
    void testIntegrationQuestionnaire01() {
        campaignExistenceService.setCampaignExist(false);
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(QUESTIONNAIRE_ID, LABEL,
                JsonNodeFactory.instance.objectNode(), new HashSet<>(), CAMPAIGN_ID);
        List<IntegrationResult> results = integrationService.create(questionnaire);
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.getId()).isEqualTo(QUESTIONNAIRE_ID);
        assertThat(result.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.getCause()).isEqualTo(String.format(IntegrationResultLabel.CAMPAIGN_DO_NOT_EXIST, CAMPAIGN_ID));
    }

    @Test
    @DisplayName("On save questionnaire, when nomenclature does not exist, return integration error")
    void testIntegrationQuestionnaire04() {
        String nonExistingNomenclature1 = "non-exist-nomenclature1";
        String nonExistingNomenclature2 = "non-exist-nomenclature2";
        String existingNomenclature1 = "exist-nomenclature1";
        String existingNomenclature2 = "exist-nomenclature2";

        nomenclatureService.setNonExistingNomenclatures(List.of(nonExistingNomenclature1, nonExistingNomenclature2));
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(QUESTIONNAIRE_ID, LABEL,
                JsonNodeFactory.instance.objectNode(), Set.of(existingNomenclature1, nonExistingNomenclature1, existingNomenclature2, nonExistingNomenclature2), CAMPAIGN_ID);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        List<IntegrationResult> errorResults = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR)).toList();
        assertThat(errorResults).hasSize(2);
        IntegrationResult errorResult1 = new IntegrationResult(QUESTIONNAIRE_ID, IntegrationStatus.ERROR,
                String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nonExistingNomenclature1));
        IntegrationResult errorResult2 = new IntegrationResult(QUESTIONNAIRE_ID, IntegrationStatus.ERROR,
                String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nonExistingNomenclature2));
        assertThat(errorResults)
                .contains(errorResult1)
                .contains(errorResult2);
    }

    @Test
    @DisplayName("On save questionnaire, when questionnaire exists, return integration update")
    void testIntegrationQuestionnaire02() {
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(QUESTIONNAIRE_ID, LABEL,
                JsonNodeFactory.instance.objectNode(), new HashSet<>(), CAMPAIGN_ID);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        assertThat(questionnaireService.isUpdated()).isTrue();
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.getId()).isEqualTo(QUESTIONNAIRE_ID);
        assertThat(result.getStatus()).isEqualTo(IntegrationStatus.UPDATED);
    }

    @Test
    @DisplayName("On save questionnaire, when questionnaire does not exist, return integration create")
    void testIntegrationQuestionnaire03() {
        questionnaireExistenceService.setQuestionnaireExist(false);
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(QUESTIONNAIRE_ID, LABEL,
                JsonNodeFactory.instance.objectNode(), new HashSet<>(), CAMPAIGN_ID);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        assertThat(questionnaireService.isCreated()).isTrue();
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.getId()).isEqualTo(QUESTIONNAIRE_ID);
        assertThat(result.getStatus()).isEqualTo(IntegrationStatus.CREATED);
    }
}
