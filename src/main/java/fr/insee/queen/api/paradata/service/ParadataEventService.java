package fr.insee.queen.api.paradata.service;

public interface ParadataEventService {
    void createParadataEvent(String surveyUnitId, String paradataValue);
}
