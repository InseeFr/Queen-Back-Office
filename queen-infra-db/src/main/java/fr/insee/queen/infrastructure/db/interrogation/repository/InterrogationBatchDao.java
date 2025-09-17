package fr.insee.queen.infrastructure.db.interrogation.repository;

import fr.insee.queen.domain.interrogation.gateway.InterrogationBatchRepository;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InterrogationBatchDao implements InterrogationBatchRepository {

    private final JdbcTemplate jdbc;
    @Value("${feature.sensitive-data.enabled:false}")
    private final boolean cipherEnabled;

    @Override
    @Transactional
    public void upsertAll(List<Interrogation> interrogations) {
        final String upsertInterrogation = """
            INSERT INTO interrogation (id, survey_unit_id, campaign_id, questionnaire_model_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                survey_unit_id = EXCLUDED.survey_unit_id,
                campaign_id = EXCLUDED.campaign_id,
                questionnaire_model_id = EXCLUDED.questionnaire_model_id
        """;

        jdbc.batchUpdate(upsertInterrogation, interrogations, interrogations.size(), (preparedStatement, interrogation) -> {
            preparedStatement.setString(1, interrogation.id());
            preparedStatement.setString(2, interrogation.surveyUnitId());
            preparedStatement.setString(3, interrogation.campaignId());
            preparedStatement.setString(4, interrogation.questionnaireId());
        });



        if(cipherEnabled) {
            upsertDataCiphered(interrogations);
        } else {
            upsertData(interrogations);
        }

        deletePersonalizationIfNull(interrogations);
        upsertPersonalizationIfNotNull(interrogations);
    }

    private void upsertData(List<Interrogation> interrogations) {
        final String sqlData = """
            INSERT INTO data (id, value, interrogation_id, encrypted)
            VALUES (?, ?::jsonb, ?, 0)
            ON CONFLICT (interrogation_id) DO UPDATE SET
                value = EXCLUDED.value,
                encrypted = EXCLUDED.encrypted
        """;

        jdbc.batchUpdate(sqlData, interrogations, interrogations.size(),
                (preparedStatement, interrogation) -> {
                    preparedStatement.setObject(1, UUID.randomUUID());
                    preparedStatement.setString(2, interrogation.data().toString());
                    preparedStatement.setString(3, interrogation.id());
                }
        );
    }

    private void upsertDataCiphered(List<Interrogation> interrogations) {
        // Ici, ? est du texte JSON ; la colonne est BYTEA et la clé est dans la GUC (initSql du pool)
        final String sqlData = """
            INSERT INTO data (id, value, interrogation_id, encrypted)
            VALUES (
                ?,
                pgp_sym_encrypt(?::text, current_setting('data.encryption.key'), 's2k-count=65536'),
                ?,
                1
            )
            ON CONFLICT (interrogation_id) DO UPDATE SET
                value = EXCLUDED.value,
                encrypted = EXCLUDED.encrypted
        """;

        jdbc.batchUpdate(sqlData, interrogations, interrogations.size(),
                (preparedStatement, interrogation) -> {
                    preparedStatement.setObject(1, UUID.randomUUID());
                    preparedStatement.setObject(2, interrogation.data().toString(), Types.OTHER);
                    preparedStatement.setString(3, interrogation.id());
                }
        );
    }

    /**
     * Supprime la personnalisation pour les interrogations dont la valeur est null.
     */
    private void deletePersonalizationIfNull(List<Interrogation> interrogations) {
        List<String> idsToDelete = interrogations.stream()
                .filter(i -> i.personalization() == null)
                .map(Interrogation::id)
                .toList();

        if (!idsToDelete.isEmpty()) {
            final String deleteSql = "DELETE FROM personalization WHERE interrogation_id = ANY (?)";
            jdbc.update(con -> {
                var ps = con.prepareStatement(deleteSql);
                ps.setArray(1, con.createArrayOf("varchar", idsToDelete.toArray()));
                return ps;
            });
        }
    }

    /**
     * Insert ou met à jour la personnalisation pour les interrogations qui en ont une.
     */
    private void upsertPersonalizationIfNotNull(List<Interrogation> interrogations) {
        List<Interrogation> interrogationswithPersonnalization = interrogations.stream()
                .filter(i -> i.personalization() != null)
                .toList();

        if (!interrogationswithPersonnalization.isEmpty()) {
            final String upsertPersonalization = """
                INSERT INTO personalization (id, value, interrogation_id)
                VALUES (?, ?::jsonb, ?)
                ON CONFLICT (interrogation_id) DO UPDATE SET
                    value = EXCLUDED.value
            """;

            jdbc.batchUpdate(upsertPersonalization, interrogationswithPersonnalization, interrogationswithPersonnalization.size(), (preparedStatement, interrogation) -> {
                preparedStatement.setObject(1, UUID.randomUUID());
                preparedStatement.setString(2, interrogation.personalization().toString());
                preparedStatement.setString(3, interrogation.id());
            });
        }
    }



    @Override
    @Transactional
    public void deleteAll(List<String> interrogationIds) {
        final String deleteSql = "DELETE FROM interrogation WHERE id = ANY (?)";

        jdbc.update(con -> {
            var preparedStatement = con.prepareStatement(deleteSql);
            preparedStatement.setArray(1, con.createArrayOf("varchar", interrogationIds.toArray()));
            return preparedStatement;
        });
    }
}
