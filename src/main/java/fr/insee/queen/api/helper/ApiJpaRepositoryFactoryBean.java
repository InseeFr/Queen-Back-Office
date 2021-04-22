package fr.insee.queen.api.helper;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import fr.insee.queen.api.repository.base.ApiJpaRepository;

public class ApiJpaRepositoryFactoryBean<R extends PagingAndSortingRepository<T, I>, T,
        I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

    public ApiJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new ApiJpaRepositoryFactory<>(em);
    }

    private static class ApiJpaRepositoryFactory<T> extends JpaRepositoryFactory {

        public ApiJpaRepositoryFactory(EntityManager em) {
            super(em);
        }

        @SuppressWarnings("unchecked")
		@Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                        EntityManager em) {
            return new ApiJpaRepository<>((Class<T>) information.getDomainType(), em);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return ApiJpaRepository.class;
        }
    }
}