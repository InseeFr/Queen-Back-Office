package fr.insee.queen.application.integration.component;

import fr.insee.queen.application.configuration.properties.ApplicationProperties;
import fr.insee.queen.application.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.application.integration.component.builder.CampaignBuilder;
import fr.insee.queen.application.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.application.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipFile;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class IntegrationComponent {
    private final NomenclatureBuilder nomenclatureBuilder;
    private final CampaignBuilder campaignBuilder;
    private final QuestionnaireBuilder questionnaireBuilder;
    private final ApplicationProperties applicationProperties;

    /**
     * Try to do the full integration of a campaign.
     * Return results about the integration of each component (campaign/questionnaires/nomenclatures)
     *
     * @param integrationFile integration file
     * @param isXmlIntegration Is integration done with xml files
     * @return {@link IntegrationResultsDto} integration results
     */
    public IntegrationResultsDto integrateContext(MultipartFile integrationFile, boolean isXmlIntegration) {
        try {
            Path tempDirectoryPath = Path.of(applicationProperties.tempFolder());
            File zip = Files.createTempFile(tempDirectoryPath, UUID.randomUUID().toString(), ".temp").toFile();
            return integrateContext(zip, integrationFile, isXmlIntegration);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    /**
     * Try to do the full integration of a campaign.
     *
     * @param integrationFile integration file
     * @param isXmlIntegration Is integration done with xml files
     * @return {@link IntegrationResultsDto} integration results
     */
    private IntegrationResultsDto integrateContext(File integrationFile, MultipartFile file, boolean isXmlIntegration) {
        try (FileOutputStream o = new FileOutputStream(integrationFile)) {
            IOUtils.copy(file.getInputStream(), o);
            return doIntegration(integrationFile, isXmlIntegration);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    /**
     * Try to do the full integration of a campaign.
     *
     * @param integrationFile integration file
     * @param isXmlIntegration Is integration done with xml files
     * @return {@link IntegrationResultsDto} integration results
     */
    private IntegrationResultsDto doIntegration(File integrationFile, boolean isXmlIntegration) {
        IntegrationResultsDto result = new IntegrationResultsDto();

        try (ZipFile zf = new ZipFile(integrationFile)) {
            List<IntegrationResultUnitDto> nomenclatureResults = nomenclatureBuilder.build(zf, isXmlIntegration);
            result.setNomenclatures(nomenclatureResults);

            IntegrationResultUnitDto campaignResult = campaignBuilder.build(zf, isXmlIntegration);
            result.setCampaign(campaignResult);

            if (campaignResult.getStatus() == IntegrationStatus.ERROR) {
                return result;
            }

            List<IntegrationResultUnitDto> questionnaireResults = questionnaireBuilder.build(campaignResult.getId(), zf, isXmlIntegration);
            result.setQuestionnaireModels(questionnaireResults);

            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }
}

