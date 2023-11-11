package fr.insee.queen.api.integration.controller.component;

import fr.insee.queen.api.integration.controller.component.builder.CampaignBuilder;
import fr.insee.queen.api.integration.controller.component.builder.NomenclatureBuilder;
import fr.insee.queen.api.integration.controller.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationComponentException;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultsDto;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipFile;

@Component
@Slf4j
@AllArgsConstructor
@Transactional
public class IntegrationComponent {
    private final NomenclatureBuilder nomenclatureBuilder;
    private final CampaignBuilder campaignBuilder;
    private final QuestionnaireBuilder questionnaireBuilder;

    /**
     * Try to do the full integration of a campaign.
     *
     * @param integrationFile integration file
     * @return {@link IntegrationResultsDto} integration results
     */
    public IntegrationResultsDto integrateContext(MultipartFile integrationFile) {
        try {
            File zip = Files.createTempFile(UUID.randomUUID().toString(), "temp").toFile();
            return integrateContext(zip, integrationFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    /**
     * Try to do the full integration of a campaign.
     *
     * @param integrationFile integration file
     * @return {@link IntegrationResultsDto} integration results
     */
    private IntegrationResultsDto integrateContext(File integrationFile, MultipartFile file) throws IOException {
        try (FileOutputStream o = new FileOutputStream(integrationFile)) {
            IOUtils.copy(file.getInputStream(), o);
            return doIntegration(integrationFile);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    /**
     * Try to do the full integration of a campaign.
     *
     * @param integrationFile integration file
     * @return {@link IntegrationResultsDto} integration results
     */
    private IntegrationResultsDto doIntegration(File integrationFile) throws JSONException {
        IntegrationResultsDto result = new IntegrationResultsDto();

        try (ZipFile zf = new ZipFile(integrationFile)) {
            List<IntegrationResultUnitDto> nomenclatureResults = nomenclatureBuilder.build(zf);
            result.nomenclatures(nomenclatureResults);

            IntegrationResultUnitDto campaignResult = campaignBuilder.build(zf);
            result.campaign(campaignResult);

            if (campaignResult.status() == IntegrationStatus.ERROR) {
                return result;
            }

            List<IntegrationResultUnitDto> questionnaireResults = questionnaireBuilder.build(campaignResult.id(), zf);
            result.questionnaireModels(questionnaireResults);

            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }
}

