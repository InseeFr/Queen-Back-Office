package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;

public record QuestionnaireModelInputDto(
	String idQuestionnaireModel,
	String label,
	ObjectNode value,
	Set<String> requiredNomenclatureIds) {}
