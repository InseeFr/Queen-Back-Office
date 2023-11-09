package fr.insee.queen.api.dto.integration;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IntegrationResultSuccessUnitDto extends IntegrationResultUnitDto {

	private IntegrationResultSuccessUnitDto(String id, IntegrationStatus status, String cause) {
		super(id, status, cause);
	}

	public static IntegrationResultSuccessUnitDto integrationResultUnitCreated(String id) {
		return new IntegrationResultSuccessUnitDto(id, IntegrationStatus.CREATED, null);
	}

	public static IntegrationResultSuccessUnitDto integrationResultUnitUpdated(String id) {
		return new IntegrationResultSuccessUnitDto(id, IntegrationStatus.UPDATED, null);
	}
}

