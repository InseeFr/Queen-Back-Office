package fr.insee.queen.api.integration.service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResult {
    private String id;
    private IntegrationStatus status;
    private String cause;
}
