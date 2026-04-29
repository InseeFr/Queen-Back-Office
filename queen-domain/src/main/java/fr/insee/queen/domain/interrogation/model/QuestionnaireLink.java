package fr.insee.queen.domain.interrogation.model;

public record QuestionnaireLink(
        String interrogationId,
        String questionnaireId
) {
}