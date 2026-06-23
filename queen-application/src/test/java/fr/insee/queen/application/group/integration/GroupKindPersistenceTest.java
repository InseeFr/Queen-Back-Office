package fr.insee.queen.application.group.integration;

import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.repository.jpa.GroupJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GroupKindPersistenceTest {

    @Autowired
    private GroupJpaRepository groupJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @ParameterizedTest
    @ValueSource(strings = {"CAMPAIGN", "PARTITION"})
    @DisplayName("Given a group with a kind value, when persisted and retrieved, the kind is preserved")
    void should_persist_kind_correctly(String kind) {
        // Given
        GroupDB group = new GroupDB("kind-test-group", "Test Group", Set.of(), kind);

        // When
        groupJpaRepository.save(group);
        entityManager.flush();
        entityManager.clear();

        // Then
        GroupDB retrieved = groupJpaRepository.findById("kind-test-group").orElseThrow();
        assertThat(retrieved.getKind()).isEqualTo(kind);
    }
}
