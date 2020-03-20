package fr.insee.queen.queen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.Comment;
import fr.insee.queen.queen.dto.operation.CommentDto;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<CommentDto> findDtoBy();

	CommentDto findDtoByReportingUnit_id(Long id);

	Optional<Comment> findByReportingUnit_id(Long id);
}
