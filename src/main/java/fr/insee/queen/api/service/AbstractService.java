package fr.insee.queen.api.service;


import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractService<T, I> implements BaseService<T, I> {
    protected abstract JpaRepository<T, I> getRepository();

}
