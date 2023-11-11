package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.builder.CampaignBuilder;
import fr.insee.queen.api.controller.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.api.controller.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationStatus;
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
     * @param integrationFile integration file
     * @return {@link IntegrationResultDto} integration results
     */
    public IntegrationResultDto integrateContext(MultipartFile integrationFile) {
        try {
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            return integrateContext(zip, integrationFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    /**
     * Try to do the full integration of a campaign.
     * @param integrationFile integration file
     * @return {@link IntegrationResultDto} integration results
     */
    private IntegrationResultDto integrateContext(File integrationFile, MultipartFile file) throws IOException {
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
     * @param integrationFile integration file
     * @return {@link IntegrationResultDto} integration results
     */
    private IntegrationResultDto doIntegration(File integrationFile) throws JSONException {
        IntegrationResultDto result = new IntegrationResultDto();

        try(ZipFile zf = new ZipFile(integrationFile)){
            List<IntegrationResultUnitDto> nomenclatureResults = nomenclatureBuilder.build(zf);
            result.nomenclatures(nomenclatureResults);

            IntegrationResultUnitDto campaignResult = campaignBuilder.build(zf);
            result.campaign(campaignResult);

            if(campaignResult.status() == IntegrationStatus.ERROR) {
                return result;
            }

            List<IntegrationResultUnitDto> questionnaireResults = questionnaireBuilder.build(campaignResult.id(), zf);
            result.questionnaireModels(questionnaireResults);

            return result;
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }
}

