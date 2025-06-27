package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CommentApiService implements CommentService {
    private final InterrogationRepository interrogationRepository;

    @Override
    public ObjectNode getComment(String interrogationId) {
        return interrogationRepository
                .findComment(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment not found for interrogation %s", interrogationId)));
    }

    @Override
    @Transactional
    public void updateComment(String interrogationId, ObjectNode commentValue) {
        interrogationRepository.saveComment(interrogationId, commentValue);
    }
}
