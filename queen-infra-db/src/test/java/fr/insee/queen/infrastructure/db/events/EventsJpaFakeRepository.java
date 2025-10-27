package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class EventsJpaFakeRepository implements EventsJpaRepository<OutboxDB, UUID> {
    @Getter
    private boolean created = false;

    @Override
    public void createEvent(UUID id, ObjectNode event) {
        this.created = true;
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAllInBatch(Iterable<OutboxDB> entities) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public OutboxDB getOne(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public OutboxDB getById(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public OutboxDB getReferenceById(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> long count(Example<S> example) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> S save(S entity) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public <S extends OutboxDB> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public Optional<OutboxDB> findById(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public boolean existsById(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public List<OutboxDB> findAll() {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public List<OutboxDB> findAllById(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteById(UUID uuid) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void delete(OutboxDB entity) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAll(Iterable<? extends OutboxDB> entities) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public List<OutboxDB> findAll(Sort sort) {
        throw new UnsupportedOperationException("Not needed for test");
    }

    @Override
    public Page<OutboxDB> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Not needed for test");
    }
}
