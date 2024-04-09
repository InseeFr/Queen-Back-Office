package fr.insee.queen.domain.campaign.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface MetadataService {
    ObjectNode getMetadata(String campaignId);

    ObjectNode getMetadataByQuestionnaireId(String questionnaireId);
}
