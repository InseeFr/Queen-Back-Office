package fr.insee.queen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.domain.Operation;
import fr.insee.queen.dto.operation.OperationDto;

public interface OperationRepository extends JpaRepository<Operation, Long> {
	List<OperationDto> findDtoBy();
}
