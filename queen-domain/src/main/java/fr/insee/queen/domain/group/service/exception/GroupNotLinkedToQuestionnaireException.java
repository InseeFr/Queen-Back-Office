package fr.insee.queen.domain.group.service.exception;

public class GroupNotLinkedToQuestionnaireException extends RuntimeException {
    public static final String MESSAGE = "Questionnaire %s is not linked to group %s";

    public GroupNotLinkedToQuestionnaireException(String groupId, String questionnaireId) {
        super(String.format(MESSAGE, questionnaireId, groupId));
    }
}
