package fr.insee.queen.domain.campaign.service;

import tools.jackson.databind.node.ObjectNode;

public interface MetadataService {
    ObjectNode getMetadata(String campaignId);

    ObjectNode getMetadataByQuestionnaireId(String questionnaireId);
}
