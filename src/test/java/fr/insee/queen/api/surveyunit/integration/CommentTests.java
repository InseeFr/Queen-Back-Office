package fr.insee.queen.api.surveyunit.integration;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.api.utils.JsonTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase()
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentTests {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER);

    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

    @Test
    void on_get_comment_return_comment() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("db/dataset/comment.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/pl_op/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_comment_when_anonymousUser_return_401() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_comment_comment_is_updated() throws Exception {
        String surveyUnitId = "12";
        String commentJson = JsonTestHelper.getResourceFileAsString("db/dataset/comment.json");
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(commentJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/comment")
                        .content(commentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(commentJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_update_comment_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(put("/api/survey-unit/not-exist/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/survey-unit/not-exist/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/invalid_identifier/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_comment_when_comment_not_json_object_node_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/12/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(nonAdminUser))
                )
                .andExpect(status().isBadRequest());
    }
}
