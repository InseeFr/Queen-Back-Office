package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;


public interface CommentService {
    ObjectNode getComment(String interrogationId);

    void updateComment(String interrogationId, ObjectNode commentValue);
}
