package fr.insee.queen.api.dto.integration;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IntegrationResultErrorUnitDto extends IntegrationResultUnitDto {
	public IntegrationResultErrorUnitDto(String id, String cause) {
		super(id, IntegrationStatus.ERROR, cause);
	}
}

