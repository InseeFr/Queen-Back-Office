package fr.insee.queen.queen.dto.operation;

import fr.insee.queen.queen.domain.Version;

public interface DataDto {
	Version getVersion();
	String getValue();
}
