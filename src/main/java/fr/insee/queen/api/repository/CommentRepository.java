package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.dto.comment.CommentDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

	/**
	* This method retrieve the Comment for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link CommentDto}
	*/
	Optional<CommentDto> findBySurveyUnitId(String id);
}
