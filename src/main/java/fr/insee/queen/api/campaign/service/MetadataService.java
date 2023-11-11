package fr.insee.queen.api.campaign.service;

public interface MetadataService {
    String getMetadata(String campaignId);

    String getMetadataByQuestionnaireId(String questionnaireId);
}
