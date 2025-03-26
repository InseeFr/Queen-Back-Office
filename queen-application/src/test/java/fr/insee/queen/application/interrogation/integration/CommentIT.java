package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CommentIT {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_comment_return_comment() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("interrogation/comment.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/interrogation/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/interrogation/pl!op/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_comment_when_anonymousUser_return_401() throws Exception {
        mockMvc.perform(get("/api/interrogation/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_comment_comment_is_updated() throws Exception {
        String interrogationId = "d98d28c2-1535-4fc8-a405-d6a554231bbc";
        String commentJson = JsonTestHelper.getResourceFileAsString("interrogation/comment.json");
        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(commentJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/interrogation/" + interrogationId + "/comment")
                        .content(commentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(commentJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_update_comment_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(put("/api/interrogation/not-exist/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/interrogation/not-exist/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/invalid!identifier/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_comment_when_comment_not_json_object_node_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/12/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }
}
