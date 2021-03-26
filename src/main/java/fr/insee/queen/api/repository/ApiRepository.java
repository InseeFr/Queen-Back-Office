package fr.insee.queen.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApiRepository<T, I> extends PagingAndSortingRepository<T, I> {
}
