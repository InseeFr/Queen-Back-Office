package fr.insee.queen.queen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.ReportingUnit;
import fr.insee.queen.queen.dto.reportingunit.ReportingUnitDto;
/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface ReportingUnitRepository extends JpaRepository<ReportingUnit, Long> {
	/**
	* This method retrieve all ReportingUnit in DB
	* 
	* @return List of all {@link ReportingUnit}
	*/
	List<ReportingUnitDto> findDtoBy();
	/**
	* This method retrieve all reporting units associated to a specific operation
	* 
	* @param id id of the operation
	* @return {@link ReportingUnitDto}
	*/
	List<ReportingUnitDto> findDtoByOperation_id(String id);
}
