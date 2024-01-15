package fr.insee.queen.domain.paradata.service;

public interface ParadataEventService {
    void createParadataEvent(String surveyUnitId, String paradataValue);
}
