package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public record QuestionnaireModelInputDto(
	String idQuestionnaireModel,
	String label,
	JsonNode value,
	Set<String> requiredNomenclatureIds) {}
