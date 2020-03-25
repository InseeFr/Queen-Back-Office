package fr.insee.queen.api.dto.data;

import fr.insee.queen.api.domain.Version;

public interface DataDto {
	Version getVersion();
	String getValue();
}
