package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.campaign.service.dummy.CampaignFakeService;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.domain.campaign.service.exception.CampaignDeletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CampaignDeleteWithForceControllerTest {

    private CampaignDeleteWithForceController campaignController;
    private PilotageFakeComponent pilotageComponent;
    private CampaignFakeService campaignService;
    private final String campaignId = "11";

    @BeforeEach
    void init() {
        campaignService = spy(new CampaignFakeService());
        pilotageComponent = new PilotageFakeComponent();
        campaignController = new CampaignDeleteWithForceController(campaignService, pilotageComponent);
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController.deleteCampaignById(true, campaignId);
        verify(campaignService, times(1)).delete(campaignId, true);
    }

    @Test
    @DisplayName("On deletion, when campaign is closed, deletion is done")
    void testDeletion_02() {
        campaignController.deleteCampaignById(false, campaignId);
        verify(campaignService, times(1)).delete(campaignId, true);
    }

    @Test
    @DisplayName("On deletion, when campaign is opened, deletion is aborted")
    void testDeletionException() {
        pilotageComponent.setCampaignClosed(false);
        assertThatThrownBy(() -> campaignController.deleteCampaignById(false, campaignId))
                .isInstanceOf(CampaignDeletionException.class);
        verifyNoInteractions(campaignService);
    }


}
