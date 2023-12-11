package fr.insee.queen.api.campaign.controller;

import fr.insee.queen.api.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.api.campaign.service.exception.CampaignDeletionException;
import fr.insee.queen.api.pilotage.controller.dummy.PilotageFakeComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignControllerTest {

    private CampaignController campaignController;
    private PilotageFakeComponent pilotageComponent;
    private CampaignFakeService campaignService;

    @BeforeEach
    public void init() {
        campaignService = new CampaignFakeService();
        pilotageComponent = new PilotageFakeComponent();
        campaignController = new CampaignController(campaignService, pilotageComponent);
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController.deleteCampaignById(true, "11");
        assertThat(campaignService.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is closed, deletion is done")
    void testDeletion_02() {
        campaignController.deleteCampaignById(false, "11");
        assertThat(campaignService.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("On deletion, when campaign is opened, deletion is aborted")
    void testDeletionException() {
        pilotageComponent.setCampaignClosed(false);
        assertThatThrownBy(() -> campaignController.deleteCampaignById(false, "11"))
                .isInstanceOf(CampaignDeletionException.class);
    }


}
