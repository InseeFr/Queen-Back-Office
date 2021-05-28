package fr.insee.queen.api.service;

import fr.insee.queen.api.repository.ApiRepository;

public abstract class AbstractService<T, I> implements BaseService<T, I> {
    protected abstract ApiRepository<T, I> getRepository();

}
