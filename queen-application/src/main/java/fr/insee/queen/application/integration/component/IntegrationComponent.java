package fr.insee.queen.application.integration.component;

import fr.insee.queen.application.configuration.properties.ApplicationProperties;
import fr.insee.queen.application.integration.component.builder.GroupBuilder;
import fr.insee.queen.application.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.application.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.application.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import fr.insee.queen.domain.group.gateway.GroupKindProvider;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class IntegrationComponent {
    private final NomenclatureBuilder nomenclatureBuilder;
    private final GroupBuilder groupBuilder;
    private final QuestionnaireBuilder questionnaireBuilder;
    private final ApplicationProperties applicationProperties;
    private final GroupKindProvider groupKindProvider;

    public IntegrationResultsDto integrateContext(MultipartFile integrationFile) {
        try {
            Path tempDirectoryPath = Path.of(applicationProperties.tempFolder());
            File zip = Files.createTempFile(tempDirectoryPath, UUID.randomUUID().toString(), ".temp").toFile();
            return integrateContext(zip, integrationFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    private IntegrationResultsDto integrateContext(File integrationFile, MultipartFile file) {
        try (FileOutputStream o = new FileOutputStream(integrationFile)) {
            IOUtils.copy(file.getInputStream(), o);
            return doIntegration(integrationFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    private IntegrationResultsDto doIntegration(File integrationFile) {
        IntegrationResultsDto result = new IntegrationResultsDto();

        try (ZipFile zf = new ZipFile(integrationFile)) {
            List<IntegrationResultUnitDto> nomenclatureResults = nomenclatureBuilder.build(zf);
            result.setNomenclatures(nomenclatureResults);

            List<IntegrationResultUnitDto> questionnaireResults = questionnaireBuilder.build(zf);
            result.setQuestionnaireModels(questionnaireResults);

            boolean anyQuestionnaireInError = questionnaireResults.stream()
                    .anyMatch(r -> r.getStatus() == IntegrationStatus.ERROR);

            if (anyQuestionnaireInError) {
                result.setGroups(List.of(IntegrationResultUnitDto.integrationResultUnitError(
                        null, IntegrationResultLabel.GROUPS_SKIPPED_QUESTIONNAIRE_ERRORS.formatted(
                                groupKindProvider
                                        .getKind()
                                        .getForLabel()))));
                return result;
            }

            Set<String> questionnaireIds = questionnaireResults.stream()
                    .map(IntegrationResultUnitDto::getId)
                    .collect(Collectors.toSet());

            List<IntegrationResultUnitDto> groupsResult = groupBuilder.build(zf, questionnaireIds);
            result.setGroups(groupsResult);

            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }
}
