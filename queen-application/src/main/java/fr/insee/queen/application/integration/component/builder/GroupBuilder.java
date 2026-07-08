package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

public interface GroupBuilder {
    /**
     * Create the campaign or the partitions
     *
     * @param integrationZipFile zip file containing all infos for integration
     * @param questionnaireIds   ids of questionnaire models to associate with the campaign
     * @return List of {@link IntegrationResultUnitDto} integration results
     */
    List<IntegrationResultUnitDto> build(ZipFile integrationZipFile, Set<String> questionnaireIds);
}

