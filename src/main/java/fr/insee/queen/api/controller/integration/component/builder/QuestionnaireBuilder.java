package fr.insee.queen.api.controller.integration.component.builder;

import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;

import java.util.List;
import java.util.zip.ZipFile;


public interface QuestionnaireBuilder {
    List<IntegrationResultUnitDto> build(String campaignId, ZipFile zf);
}
