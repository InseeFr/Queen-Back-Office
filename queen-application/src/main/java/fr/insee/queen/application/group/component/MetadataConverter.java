package fr.insee.queen.application.group.component;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.interrogation.dto.output.*;

public interface MetadataConverter {
    MetadataDto convert(ObjectNode metadataNode);
}
