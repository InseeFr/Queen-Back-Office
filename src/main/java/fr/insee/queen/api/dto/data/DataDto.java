package fr.insee.queen.api.dto.data;

import org.json.simple.JSONObject;

import fr.insee.queen.api.domain.Version;

public interface DataDto {
	Version getVersion();
	JSONObject getValue();
}
