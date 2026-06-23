package fr.insee.queen.application.group.integration;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.group.dto.input.GroupCreationRequest;
import fr.insee.queen.application.group.dto.input.GroupCreationRequestV2;
import fr.insee.queen.application.group.dto.input.MetadataCreationData;
import fr.insee.queen.application.group.dto.input.QuestionnaireModelCreationData;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.InterrogationJpaRepository;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GroupIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InterrogationJpaRepository interrogationRepository;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_groups_return_json_groups() throws Exception {
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'SIMPSONS2020X00')].questionnaireIds[*]").value(containsInAnyOrder("simpsons", "simpsonsV2")))
                .andExpect(jsonPath("$[?(@.id == 'VQS2021X00')].questionnaireIds[*]").value(containsInAnyOrder("VQS2021X00")))
                .andExpect(jsonPath("$[?(@.id == 'LOG2021X11Web')].questionnaireIds[*]").value(containsInAnyOrder("LOG2021X11Web")))
                .andExpect(jsonPath("$[?(@.id == 'LOG2021X11Tel')].questionnaireIds[*]").value(containsInAnyOrder("LOG2021X11Tel")));
    }

    @Test
    void on_get_groups_ids_return_json_groups() throws Exception {
        mockMvc.perform(get("/api/campaigns/ids")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(4)))
                .andExpect(jsonPath("$[0].id", is("SIMPSONS2020X00")))
                .andExpect(jsonPath("$[1].id", is("VQS2021X00")))
                .andExpect(jsonPath("$[2].id", is("LOG2021X11Web")))
                .andExpect(jsonPath("$[3].id", is("LOG2021X11Tel")));
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_groups_return_200() throws Exception {
        String questionnaireId = "questionnaire-for-group-creation";
        ObjectNode questionnaireJson = JsonTestHelper.getResourceFileAsObjectNode("questionnaire/simpsons.json");
        Set<String> nomenclatures = Set.of("cities2019", "regions2019");
        QuestionnaireModelCreationData questionnaire = new QuestionnaireModelCreationData(questionnaireId, "label questionnaire", questionnaireJson, nomenclatures);
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))

                )
                .andExpect(status().isCreated());

        String groupName = "GROUP-12345";
        Set<String> questionnaireIds = Set.of(questionnaireId);

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("group/metadata/metadata.json");
        MetadataCreationData metadata = new MetadataCreationData(metadataNode);
        GroupCreationRequest group = new GroupCreationRequest(groupName, "label group",  questionnaireIds, metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());

        String expressionFilter = "$[?(@.id == '" + groupName + "')].questionnaireIds[*]";
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(expressionFilter).value(containsInAnyOrder(questionnaireIds.toArray())));
    }

    @Test
    void on_create_groups_when_group_already_exist_return_400() throws Exception {
        MetadataCreationData metadata = new MetadataCreationData(JsonNodeFactory.instance.objectNode());
        GroupCreationRequest group = new GroupCreationRequest("VQS2021X00", "label group",  Set.of("simpsons", "simpsonsV2"), metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_groups_when_group_invalid_identifier_return_400() throws Exception {
        MetadataCreationData metadata = new MetadataCreationData(JsonNodeFactory.instance.objectNode());
        GroupCreationRequest group = new GroupCreationRequest("group_1234", "label group",  Set.of("simpsons", "simpsonsV2"), metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_group_when_group_exist_return_group() throws Exception {
        String groupName = "SIMPSONS2020X00";

        String expectedResult = """
                {
                  "id": "SIMPSONS2020X00",
                  "questionnaireIds": [
                    "simpsonsV2",
                    "simpsons"
                  ],
                  "metadata": {}
                }""";
        MvcResult result = mockMvc.perform(get("/api/admin/campaigns/" + groupName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();
        JSONAssert.assertEquals(expectedResult, result.getResponse().getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_group_when_group_not_exist_return_404() throws Exception {
        String groupName = "GROUP-TEST";

        mockMvc.perform(get("/api/admin/campaigns/" + groupName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_create_groups_when_questionnaire_not_exist_return_400() throws Exception {
        String groupName = "GROUP-TEST";
        Set<String> questionnaireIds = Set.of("Hello", "Plip");

        MetadataCreationData metadata = new MetadataCreationData(JsonNodeFactory.instance.objectNode());
        GroupCreationRequest group = new GroupCreationRequest(groupName, "label group",  questionnaireIds, metadata);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_groups_when_user_not_authorized_return_403() throws Exception {
        GroupCreationRequest group = new GroupCreationRequest("VQS2021X00", "label group",  Set.of("simpsons", "simpsonsV2"), null);
        mockMvc.perform(post("/api/campaigns")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_create_group_return_200() throws Exception {
        String questionnaireId = "questionnaire-for-group-creation";
        ObjectNode questionnaireJson = JsonTestHelper.getResourceFileAsObjectNode("questionnaire/simpsons.json");
        Set<String> nomenclatures = Set.of("cities2019", "regions2019");
        QuestionnaireModelCreationData questionnaire = new QuestionnaireModelCreationData(questionnaireId, "label questionnaire", questionnaireJson, nomenclatures);
        mockMvc.perform(post("/api/questionnaire-models")
                        .content(JsonTestHelper.getObjectAsJsonString(questionnaire))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))

                )
                .andExpect(status().isCreated());

        String groupName = "GROUP-12345";
        Set<String> questionnaireIds = Set.of(questionnaireId);

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("group/metadata/metadata.json");
        GroupCreationRequestV2 group = new GroupCreationRequestV2(groupName, "label group", questionnaireIds, metadataNode);
        mockMvc.perform(post("/api/campaign")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());

        String expressionFilter = "$[?(@.id == '" + groupName + "')].questionnaireIds[*]";
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(expressionFilter).value(containsInAnyOrder(questionnaireIds.toArray())));
    }

    @Test
    void on_create_group_when_group_already_exist_return_400() throws Exception {
        GroupCreationRequestV2 group = new GroupCreationRequestV2("VQS2021X00", "label group",  Set.of("simpsons", "simpsonsV2"), null);
        mockMvc.perform(post("/api/campaign")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_group_when_group_invalid_identifier_return_400() throws Exception {
        GroupCreationRequestV2 group = new GroupCreationRequestV2("group_1234", "label group",  Set.of("simpsons", "simpsonsV2"), null);
        mockMvc.perform(post("/api/campaign")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_group_when_questionnaire_not_exist_return_400() throws Exception {
        String groupName = "GROUP-TEST";
        Set<String> questionnaireIds = Set.of("Hello", "Plip");

        GroupCreationRequestV2 group = new GroupCreationRequestV2(groupName, "label group",  questionnaireIds, null);
        mockMvc.perform(post("/api/campaign")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_create_group_when_user_not_authorized_return_403() throws Exception {
        GroupCreationRequestV2 group = new GroupCreationRequestV2("VQS2021X00", "label group",  Set.of("simpsons", "simpsonsV2"), null);
        mockMvc.perform(post("/api/campaign")
                        .content(JsonTestHelper.getObjectAsJsonString(group))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_delete_group_process_deletion() throws Exception {
        String groupName = "LOG2021X11Web";
        interrogationRepository.deleteInterrogations(groupName);
        mockMvc.perform(delete("/api/campaign/" + groupName)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").value(not(containsInAnyOrder(groupName))));
    }

    @Test
    void on_delete_group_when_group_invalid_identifier_return_400() throws Exception {
        mockMvc.perform(delete("/api/campaign/invalid!identifier")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_delete_group_when_group_not_exist_return_404() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-group")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_delete_group_when_user_not_authorized_return_403() throws Exception {
        mockMvc.perform(delete("/api/campaign/non-existing-group")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void on_get_groups_when_user_not_authorized_return_403() throws Exception {
        mockMvc.perform(get("/api/admin/campaigns")
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
