package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.ObjectNode;


public interface CommentService {
    ObjectNode getComment(String surveyUnitId);

    void updateComment(String surveyUnitId, ObjectNode commentValue);
}
