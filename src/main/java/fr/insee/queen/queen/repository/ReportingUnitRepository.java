package fr.insee.queen.queen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.ReportingUnit;
import fr.insee.queen.queen.dto.operation.ReportingUnitDto;

public interface ReportingUnitRepository extends JpaRepository<ReportingUnit, Long> {
	List<ReportingUnitDto> findDtoBy();

	List<ReportingUnitDto> findDtoByOperation_id(String idOperation);
}
