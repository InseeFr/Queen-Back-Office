package fr.insee.queen.api.surveyunit.integration;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.utils.AuthenticatedUserTestHelper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();
    private final Authentication adminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.WEBCLIENT);
    private final Authentication nonAdminUser = authenticatedUserTestHelper.getAuthenticatedUser(
            AuthorityRoleEnum.REVIEWER,
            AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
            AuthorityRoleEnum.INTERVIEWER);
    private final Authentication anonymousUser = authenticatedUserTestHelper.getNotAuthenticatedUser();

    @Test
    @DisplayName("on integrate context, when non admin user return 403")
    void integrateContext01() throws Exception {
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(nonAdminUser))
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
                        .with(authentication(anonymousUser))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("on integrate context, return integration state")
    void integrateContext03() throws Exception {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("integration/integration-component.zip");
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, zipInputStream
        );

        MvcResult result = mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(adminUser))
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
                        { "id":"simpsons-2023-v1","status":"ERROR","cause":"Questionnaire model has campaign id SIMPSONS2020X00 while campaign in zip has id SIMPSONS2023X00"},
                        { "id":"simpsons-2023-v2", "status":"ERROR", "cause":"Questionnaire model file 'simpsons-v2' could not be found in input zip" }
                    ]
                }""";
        JSONAssert.assertEquals(expectedResult, content, JSONCompareMode.STRICT);
    }
}
