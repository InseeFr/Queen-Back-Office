package fr.insee.queen.api.repository.base;

import fr.insee.queen.api.repository.ApiRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;

@NoRepositoryBean
public class ApiJpaRepository<T, I> extends SimpleJpaRepository<T, I> implements ApiRepository<T, I> {
    public ApiJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
}
