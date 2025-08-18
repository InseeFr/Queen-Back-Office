package fr.insee.queen.application.campaign.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.interrogation.dto.output.*;

public interface MetadataConverter {
    MetadataDto convert(ObjectNode metadataNode);
}
