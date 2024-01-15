package fr.insee.queen.domain.campaign.service;

public interface MetadataService {
    String getMetadata(String campaignId);

    String getMetadataByQuestionnaireId(String questionnaireId);
}
