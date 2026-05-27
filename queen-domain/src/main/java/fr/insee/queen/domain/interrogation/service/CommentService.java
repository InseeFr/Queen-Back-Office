package fr.insee.queen.domain.interrogation.service;

import tools.jackson.databind.node.ObjectNode;


public interface CommentService {
    ObjectNode getComment(String interrogationId);

    void updateComment(String interrogationId, ObjectNode commentValue);
}
