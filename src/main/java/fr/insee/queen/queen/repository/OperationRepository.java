package fr.insee.queen.queen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.Operation;
import fr.insee.queen.queen.dto.operation.OperationDto;

public interface OperationRepository extends JpaRepository<Operation, String> {
	List<OperationDto> findDtoBy();
	
}
