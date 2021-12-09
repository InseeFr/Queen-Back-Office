package fr.insee.queen.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.dto.comment.CommentDto;

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
	* This method retrieve all Comment  in DB
	* 
	* @return List of all {@link CommentDto}
	*/
	List<CommentDto> findDtoBy();
	/**
	* This method retrieve the Comment for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link CommentDto}
	*/
	CommentDto findDtoBySurveyUnitId(String id);
	/**
	* This method retrieve the Comment for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link Comment}
	*/
	Optional<Comment> findBySurveyUnitId(String id);
}
