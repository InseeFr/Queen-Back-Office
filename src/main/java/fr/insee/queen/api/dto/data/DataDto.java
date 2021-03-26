package fr.insee.queen.api.dto.data;


import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Version;

public interface DataDto {
	Version getVersion();
	JsonNode getValue();
}
