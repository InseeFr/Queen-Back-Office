package fr.insee.queen.queen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.queen.domain.Data;
import fr.insee.queen.queen.dto.operation.DataDto;

public interface DataRepository extends JpaRepository<Data, Long> {
	List<DataDto> findDtoBy();

	DataDto findDtoByReportingUnit_id(Long id);
	Optional<Data> findByReportingUnit_id(Long id);
}
