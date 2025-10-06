package fr.insee.queen.application.interrogation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.interrogation.dto.input.InterrogationBatchInput;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class InterrogationBatchCommonAssertions {
    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper om = new ObjectMapper();
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    void shouldCreateUpdateDeleteInterrogations(boolean isCiphered) throws Exception {
        // given: 2 interrogations to create
        ObjectNode d1 = JsonNodeFactory.instance.objectNode().putObject("COLLECTED");
        ObjectNode d2 = JsonNodeFactory.instance.objectNode().putObject("EXTERNAL");

        var in1 = new InterrogationBatchInput(UUID.randomUUID().toString(),"SU1","simpsonsV2",
                JsonNodeFactory.instance.arrayNode(), d1, UUID.randomUUID().toString());

        var in2 = new InterrogationBatchInput(UUID.randomUUID().toString(),"SU2","simpsons",
                JsonNodeFactory.instance.arrayNode(), d2, UUID.randomUUID().toString());

        String payloadCreate = om.writeValueAsString(List.of(in1, in2));

        // when: create both
        mockMvc.perform(post("/api/campaigns/{id}/interrogations", "SIMPSONS2020X00")
                        .contentType("application/json")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                        .content(payloadCreate))
                .andExpect(status().isOk());

        // then: 2 interrogations, 2 personalizations, 2 data
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from interrogation where id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from personalization where interrogation_id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from data where interrogation_id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isEqualTo(2);

        if (!isCiphered) {
            // value est en JSONB : on compare structurellement via Postgres (= sur jsonb)
            Integer match = jdbcTemplate.queryForObject(
                    """
                    select count(*)
                    from data
                    where interrogation_id = ?
                      and encrypted = 0
                      and value = ?::jsonb
                    """,
                    Integer.class,
                    in1.id(), d1.toString()
            );
            assertThat(match).isEqualTo(1);
        } else {
            // value est en BYTEA : on déchiffre puis on compare le JSON structurellement
            Integer match = jdbcTemplate.queryForObject(
                    """
                    select count(*)
                    from data
                    where interrogation_id = ?
                      and encrypted = 1
                      and (pgp_sym_decrypt(value, current_setting('data.encryption.key'))::jsonb) = ?::jsonb
                    """,
                    Integer.class,
                    in1.id(), d1.toString()
            );
            assertThat(match).isEqualTo(1);
        }


        // given: re-upsert with I2 personalization = null (must delete its row)
        var in2NoPerso = new InterrogationBatchInput(in2.id(),in2.surveyUnitId(), in2.questionnaireId(),
                null, d2, UUID.randomUUID().toString());
        String payloadUpdate = om.writeValueAsString(List.of(in1, in2NoPerso));

        // when: update batch
        mockMvc.perform(post("/api/campaigns/{id}/interrogations", "SIMPSONS2020X00")
                        .contentType("application/json")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                        .content(payloadUpdate))
                .andExpect(status().isOk());

        // then: only 1 personalization remains (for I1)
        assertThat(jdbcTemplate.queryForObject(String.format("select count(*) from personalization where interrogation_id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select count(*) from personalization where interrogation_id='" + in1.id()+"'" , Integer.class)).isEqualTo(1);

        // when: delete both in batch
        String deletePayload = om.writeValueAsString(List.of(in1.id(), in2.id()));
        mockMvc.perform(post("/api/interrogations/delete")
                        .contentType("application/json")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                        .content(deletePayload))
                .andExpect(status().isNoContent());

        // then: all gone
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from interrogation where id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from personalization where interrogation_id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject(
                String.format("select count(*) from data where interrogation_id in('%s','%s')", in1.id(), in2.id()), Integer.class)).isZero();
    }
}
