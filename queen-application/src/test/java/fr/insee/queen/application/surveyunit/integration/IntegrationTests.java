package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @DisplayName("on integrate context, when non admin user return 403")
    void integrateContext01() throws Exception {
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("on integrate context, when anonymous user return 401")
    void integrateContext02() throws Exception {
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("getPaths")
    @DisplayName("on integrate context, return integration state")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void integrateContext030(String internalZipPath, String urlPath) throws Exception {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration" + internalZipPath + "/integration-component.zip");
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, zipInputStream
        );

        MvcResult result = mockMvc.perform(multipart("/api/campaign" + urlPath + "/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                {
                    "campaign": { "id":"SIMPSONS2023X00", "status":"CREATED" },
                    "nomenclatures":[
                        { "id":"cities2019", "status":"ERROR", "cause":"A nomenclature with id cities2019 already exists"},
                        { "id":"regions2019", "status":"ERROR", "cause":"Nomenclature file 'regions2019.json' could not be found in input zip" }
                    ],
                    "questionnaireModels":[
                        { "id":"simpsons-2023-v1", "status":"CREATED"},
                        { "id":"simpsons-2023-v2", "status":"ERROR", "cause":"Questionnaire model file 'simpsons-v2' could not be found in input zip" }
                    ]
                }""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    private static Stream<Arguments> getPaths() {
        return Stream.of(
                Arguments.of("/json", ""),
                Arguments.of("/xml", "/xml")
        );
    }
}
