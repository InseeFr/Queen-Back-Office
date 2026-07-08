package fr.insee.queen.application.interrogation.controller.dummy;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.group.component.MetadataConverter;
import fr.insee.queen.application.interrogation.dto.output.MetadataDto;
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
