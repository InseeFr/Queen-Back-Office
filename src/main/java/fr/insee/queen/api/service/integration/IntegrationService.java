package fr.insee.queen.api.service.integration;

import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;

import java.util.List;


public interface IntegrationService {
    IntegrationResultUnitDto create(CampaignIntegrationInputDto campaign);
    IntegrationResultUnitDto create(NomenclatureInputDto nomenclature);
    List<IntegrationResultUnitDto> create(QuestionnaireModelIntegrationInputDto questionnaire);
}
