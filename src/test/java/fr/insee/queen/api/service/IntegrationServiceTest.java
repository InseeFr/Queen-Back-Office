package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.integration.service.IntegrationApiService;
import fr.insee.queen.api.integration.service.IntegrationService;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;
import fr.insee.queen.api.service.dummy.*;
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

    @BeforeEach
    public void init() {
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
        Nomenclature nomenclature = new Nomenclature(nomenclatureId, "label", JsonNodeFactory.instance.arrayNode().toString());
        IntegrationResult result = integrationService.create(nomenclature);
        assertThat(result.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.id()).isEqualTo(nomenclatureId);
        assertThat(result.cause()).isEqualTo(String.format(IntegrationResultLabel.NOMENCLATURE_ALREADY_EXISTS, nomenclatureId));
        assertThat(nomenclatureService.saved()).isFalse();
    }

    @Test
    @DisplayName("On create nomenclature, when nomenclature does not exist, return integration success")
    void testIntegrationNomenclature02() {
        String nomenclatureId = "id";
        nomenclatureService.nonExistingNomenclatures(List.of(nomenclatureId));
        Nomenclature nomenclature = new Nomenclature(nomenclatureId, "label", JsonNodeFactory.instance.arrayNode().toString());
        IntegrationResult result = integrationService.create(nomenclature);
        assertThat(result.status()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(result.id()).isEqualTo(nomenclatureId);
        assertThat(nomenclatureService.saved()).isTrue();
    }

    @Test
    @DisplayName("On save campaign, when campaign exists, update campaign")
    void testIntegrationCampaign01() {
        String campaignId = "id";
        campaignExistenceService.campaignExist(true);

        Campaign campaign = new Campaign(campaignId, "label", JsonNodeFactory.instance.objectNode().toString());
        IntegrationResult campaignResult = integrationService.create(campaign);
        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.UPDATED);
        assertThat(campaignResult.id()).isEqualTo(campaignId);
        assertThat(campaignService.updated()).isTrue();
    }

    @Test
    @DisplayName("On save campaign, when campaign does not exist, create campaign")
    void testIntegrationCampaign02() {
        String campaignId = "id";
        campaignExistenceService.campaignExist(false);

        Campaign campaign = new Campaign(campaignId, "label", JsonNodeFactory.instance.objectNode().toString());
        IntegrationResult campaignResult = integrationService.create(campaign);

        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(campaignResult.id()).isEqualTo(campaignId);
        assertThat(campaignService.created()).isTrue();
    }

    @Test
    @DisplayName("On save questionnaire, when campaign does not exist, return integration error")
    void testIntegrationQuestionnaire01() {
        String questionnaireId = "id-questionnaire";
        String campaignId = "id-campaign";
        campaignExistenceService.campaignExist(false);
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label",
                JsonNodeFactory.instance.objectNode().toString(), new HashSet<>(), campaignId);
        List<IntegrationResult> results = integrationService.create(questionnaire);
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.id()).isEqualTo(questionnaireId);
        assertThat(result.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.cause()).isEqualTo(String.format(IntegrationResultLabel.CAMPAIGN_DO_NOT_EXIST, campaignId));
    }

    @Test
    @DisplayName("On save questionnaire, when nomenclature does not exist, return integration error")
    void testIntegrationQuestionnaire04() {
        String questionnaireId = "id-questionnaire";
        String campaignId = "id-campaign";
        String nonExistingNomenclature1 = "non-exist-nomenclature1";
        String nonExistingNomenclature2 = "non-exist-nomenclature2";
        String existingNomenclature1 = "exist-nomenclature1";
        String existingNomenclature2 = "exist-nomenclature2";

        nomenclatureService.nonExistingNomenclatures(List.of(nonExistingNomenclature1, nonExistingNomenclature2));
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label",
                JsonNodeFactory.instance.objectNode().toString(), Set.of(existingNomenclature1, nonExistingNomenclature1, existingNomenclature2, nonExistingNomenclature2), campaignId);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        List<IntegrationResult> errorResults = results.stream()
                .filter(result -> result.status().equals(IntegrationStatus.ERROR)).toList();
        assertThat(errorResults).hasSize(2);
        IntegrationResult errorResult1 = new IntegrationResult(questionnaireId, IntegrationStatus.ERROR,
                String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nonExistingNomenclature1));
        IntegrationResult errorResult2 = new IntegrationResult(questionnaireId, IntegrationStatus.ERROR,
                String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nonExistingNomenclature2));
        assertThat(errorResults).contains(errorResult1);
        assertThat(errorResults).contains(errorResult2);
    }

    @Test
    @DisplayName("On save questionnaire, when questionnaire exists, return integration update")
    void testIntegrationQuestionnaire02() {
        String questionnaireId = "id-questionnaire";
        String campaignId = "id-campaign";
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label",
                JsonNodeFactory.instance.objectNode().toString(), new HashSet<>(), campaignId);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        assertThat(questionnaireService.updated()).isTrue();
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.id()).isEqualTo(questionnaireId);
        assertThat(result.status()).isEqualTo(IntegrationStatus.UPDATED);
    }

    @Test
    @DisplayName("On save questionnaire, when questionnaire does not exist, return integration create")
    void testIntegrationQuestionnaire03() {
        String questionnaireId = "id-questionnaire";
        String campaignId = "id-campaign";
        questionnaireExistenceService.questionnaireExist(false);
        QuestionnaireModel questionnaire = QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label",
                JsonNodeFactory.instance.objectNode().toString(), new HashSet<>(), campaignId);
        List<IntegrationResult> results = integrationService.create(questionnaire);

        assertThat(questionnaireService.created()).isTrue();
        assertThat(results).hasSize(1);
        IntegrationResult result = results.get(0);
        assertThat(result.id()).isEqualTo(questionnaireId);
        assertThat(result.status()).isEqualTo(IntegrationStatus.CREATED);
    }
}
