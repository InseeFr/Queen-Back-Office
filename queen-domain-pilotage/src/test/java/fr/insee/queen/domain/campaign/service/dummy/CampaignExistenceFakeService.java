package fr.insee.queen.domain.campaign.service.dummy;

import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import lombok.Getter;
import lombok.Setter;

public class CampaignExistenceFakeService implements CampaignExistenceService {

    @Setter
    private boolean campaignExist = true;
    @Getter
    private boolean checkCampaignNotExist = false;
    @Getter
    private boolean checkCampaignExist = false;

    @Override
    public void throwExceptionIfCampaignNotExist(String campaignId) {
        checkCampaignExist = true;
    }

    @Override
    public void throwExceptionIfCampaignAlreadyExist(String campaignId) {
        checkCampaignNotExist = true;
    }

    @Override
    public boolean existsById(String campaignId) {
        return campaignExist;
    }
}
