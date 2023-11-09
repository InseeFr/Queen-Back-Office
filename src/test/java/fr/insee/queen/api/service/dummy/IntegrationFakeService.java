package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.service.integration.IntegrationService;

import java.util.List;

public class IntegrationFakeService implements IntegrationService {
    @Override
    public IntegrationResultUnitDto create(CampaignIntegrationInputDto campaign) {
        return IntegrationResultSuccessUnitDto.integrationResultUnitCreated(campaign.id());
    }

    @Override
    public IntegrationResultUnitDto create(NomenclatureInputDto nomenclature) {
        return IntegrationResultSuccessUnitDto.integrationResultUnitCreated(nomenclature.id());
    }

    @Override
    public List<IntegrationResultUnitDto> create(QuestionnaireModelIntegrationInputDto questionnaire) {
        return List.of(IntegrationResultSuccessUnitDto.integrationResultUnitCreated(questionnaire.idQuestionnaireModel()));
    }
}
