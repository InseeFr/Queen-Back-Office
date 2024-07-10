package fr.insee.queen.application.surveyunit.controller.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.campaign.component.MetadataConverter;
import fr.insee.queen.application.surveyunit.dto.output.MetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MetadataFakeConverter implements MetadataConverter {
    @Setter
    private MetadataDto metadata;

    @Override
    public MetadataDto convert(ObjectNode metadataNode) {
        return metadata;
    }
}
