package fr.insee.queen.api.controller.integration.component.creator;

import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.campaign.CampaignService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CampaignCreator {
    private final CampaignService campaignService;
    private final CampaignExistenceService campaignExistenceService;

    public IntegrationResultUnitDto create(CampaignIntegrationInputDto campaign) {
        String id = campaign.id();

        if(campaignExistenceService.existsById(id)) {
            log.info("Updating campaign {}", id);
            campaignService.updateCampaign(CampaignIntegrationInputDto.toModel(campaign));
            return IntegrationResultSuccessUnitDto.integrationResultUnitUpdated(id, null);
        }

        log.info("Creating campaign {}", id);
        campaignService.createCampaign(CampaignIntegrationInputDto.toModel(campaign));
        return IntegrationResultSuccessUnitDto.integrationResultUnitCreated(id, null);
    }
}

