package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentTests extends ContainerConfiguration {
    @Autowired
    private MockMvc mockMvc;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    void on_get_comment_return_comment() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/survey-unit/11/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("surveyunit/comment.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void on_get_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_get_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/survey-unit/pl!op/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_get_comment_when_anonymousUser_return_401() throws Exception {
        mockMvc.perform(get("/api/survey-unit/plop/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_comment_comment_is_updated() throws Exception {
        String surveyUnitId = "12";
        String commentJson = JsonTestHelper.getResourceFileAsString("surveyunit/comment.json");
        MvcResult result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(commentJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/survey-unit/" + surveyUnitId + "/comment")
                        .content(commentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/survey-unit/" + surveyUnitId + "/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
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
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void on_update_comment_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/survey-unit/not-exist/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void on_update_comment_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/invalid!identifier/comment")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void on_update_comment_when_comment_not_json_object_node_return_400() throws Exception {
        mockMvc.perform(put("/api/survey-unit/12/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getSurveyUnitUser()))
                )
                .andExpect(status().isBadRequest());
    }
}
