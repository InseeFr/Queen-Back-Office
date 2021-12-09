package fr.insee.queen.api.service.impl;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.SurveyUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.repository.CommentRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.CommentService;

@Service
public class CommentServiceImpl extends AbstractService<Comment, UUID> implements CommentService {

    protected final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository repository) {
        this.commentRepository = repository;
    }

    @Override
    protected JpaRepository<Comment, UUID> getRepository() {
        return commentRepository;
    }

	@Override
	public Optional<Comment> findBySurveyUnitId(String id) {
		return commentRepository.findBySurveyUnitId(id);
	}

	@Override
	public void save(Comment comment) {
		commentRepository.save(comment);
	}

	public void updateComment(SurveyUnit su, JsonNode commentValue) {
		Optional<Comment> commentOptional = commentRepository.findBySurveyUnitId(su.getId());
		if (!commentOptional.isPresent()) {
			Comment newComment = new Comment();
			newComment.setSurveyUnit(su);
			newComment.setValue(commentValue);
			commentRepository.save(newComment);
			
		}else {
			commentOptional.get().setValue(commentValue);
			commentRepository.save(commentOptional.get());
		}
	}
}
