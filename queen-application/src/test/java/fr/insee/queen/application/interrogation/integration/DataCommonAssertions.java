package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.application.utils.JsonTestHelper;
import lombok.RequiredArgsConstructor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class DataCommonAssertions {

    private final MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();


    void on_get_data_return_data() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/interrogation/517046b6-bd88-47e0-838e-00d03461f592/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.getResourceFileAsString("interrogation/data.json");
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    void on_get_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(get("/api/interrogation/plop/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    void on_get_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(get("/api/interrogation/plop$/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    void cleanExtractedData() throws Exception {
        List<String> interrogationIds = List.of("517046b6-bd88-47e0-838e-00d03461f592","d98d28c2-1535-4fc8-a405-d6a554231bbc");
        String expectedResult = "{}";
        mockMvc.perform(
                delete("/api/admin/campaign/SIMPSONS2020X00/interrogations/data/extracted?start=1111111111&end=1111111118")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAuthenticatedUser(AuthorityRoleEnum.WEBCLIENT)))
                )
                .andExpect(status().isOk())
                .andReturn();

        for(String interrogationId : interrogationIds) {
            MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/data")
                            .accept(MediaType.APPLICATION_JSON)
                            .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
        }

        MvcResult result = mockMvc.perform(get("/api/interrogation/c8142dcc-c133-49aa-a969-bb9828190a2c/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    void on_update_data_data_is_updated() throws Exception {
        String interrogationId = "d98d28c2-1535-4fc8-a405-d6a554231bbc";
        String dataJson = JsonTestHelper.getResourceFileAsString("interrogation/data.json");
        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertNotEquals(dataJson, content, JSONCompareMode.NON_EXTENSIBLE);

        mockMvc.perform(put("/api/interrogation/" + interrogationId + "/data")
                        .content(dataJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(dataJson, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    void on_update_data_when_su_not_exist_return_404() throws Exception {
        mockMvc.perform(put("/api/interrogation/not-exist/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isNotFound());
    }

    void on_update_data_when_su_id_invalid_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/invalid$identifier/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    void on_update_data_when_data_not_json_object_node_return_400() throws Exception {
        mockMvc.perform(put("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/data")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    void on_get_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(get("/api/interrogation/pl_op/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    void on_update_data_when_anonymous_user_return_401() throws Exception {
        mockMvc.perform(put("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc/data")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    void updateCollectedData02() throws Exception {
        String interrogationId = "80dc2493-5258-44c5-8ec1-9c600d1df80b";
        String externalData = """
           "EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}""";
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String newCollectedVar = """
                "DTA": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "updated"
                      }
                """;

        String collectedVarToUpdate = """
                "READY": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "plop"
                      }
                """;
        String collectedVarNotTouched = """
                "PRODUCER": {
                    "EDITED": null,
                    "FORCED": null,
                    "INPUTED": null,
                    "PREVIOUS": null,
                    "COLLECTED": "Matt Groening"
                }
                """;
        String interrogationDataStateData = String.format("""
            {
                "data": {
                      %s,
                      %s
                },
                "stateData": %s
            }""", collectedVarToUpdate, newCollectedVar, stateData);

        // check it works when already collected data
        mockMvc.perform(patch("/api/interrogation/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                ).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = String.format("""
                {
                    %s,
                    "COLLECTED": {
                        %s,
                        %s,
                        %s,
                    }
                }""", externalData, collectedVarNotTouched, collectedVarToUpdate, newCollectedVar);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    void updateCollectedData01() throws Exception {
        String interrogationId = "4f612c5d-8b60-46bd-a2de-1f0d861264db";
        String externalData = """
           "EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}""";
        String stateData = """
                {
                    "state": "EXTRACTED",
                    "date": 1111111111,
                    "currentPage": "2.3#5"
                }""";

        String collectedVar1 = """
                "DAG": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "3"
                      }
                """;

        String collectedVar2 = """
                "DTA": {
                            "EDITED": null,
                            "FORCED": null,
                            "INPUTED": null,
                            "PREVIOUS": null,
                            "COLLECTED": "4"
                      }
                """;

        String interrogationDataStateData = String.format("""
            {
                "data": {
                      %s,
                      %s
                },
                "stateData": %s
            }""", collectedVar1, collectedVar2, stateData);

        mockMvc.perform(patch("/api/interrogation/" + interrogationId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(interrogationDataStateData)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/interrogation/" + interrogationId + "/data")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = String.format("""
                {
                    %s,
                    "COLLECTED": {
                        %s,
                        %s
                    }
                }""", externalData, collectedVar1, collectedVar2);
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }

    void updateCollectedDataError02() throws Exception {
        mockMvc.perform(patch("/api/interrogation/invalid$identifier")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    void updateCollectedDataError03() throws Exception {
        mockMvc.perform(patch("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc")
                        .content("[]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getInterrogationUser()))
                )
                .andExpect(status().isBadRequest());
    }

    void updateCollectedDataError04() throws Exception {
        mockMvc.perform(patch("/api/interrogation/d98d28c2-1535-4fc8-a405-d6a554231bbc")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
