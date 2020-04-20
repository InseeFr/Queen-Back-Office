package fr.insee.queen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.domain.ReportingUnit;
import fr.insee.queen.api.dto.reportingunit.ReportingUnitDto;
/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface ReportingUnitRepository extends JpaRepository<ReportingUnit, String> {
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
