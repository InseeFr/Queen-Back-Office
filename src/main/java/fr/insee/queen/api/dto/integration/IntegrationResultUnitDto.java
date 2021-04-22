package fr.insee.queen.api.dto.integration;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.queen.api.domain.IntegrationStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationResultUnitDto {
	private String id;
	private IntegrationStatus status;
	private String cause;

	public IntegrationResultUnitDto() {
		super();
	}
	
	public IntegrationResultUnitDto(String id, IntegrationStatus status, String cause) {
		super();
		this.id = id;
		this.status = status;
		this.cause = cause;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IntegrationStatus getStatus() {
		return status;
	}

	public void setStatus(IntegrationStatus status) {
		this.status = status;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}
	


}
