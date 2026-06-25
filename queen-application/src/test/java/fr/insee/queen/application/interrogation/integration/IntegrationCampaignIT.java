package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "application.group.kind=CAMPAIGN")
class IntegrationCampaignIT {

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

    @Test
    @DisplayName("on integrate context, return integration state")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void integrateContext03() throws Exception {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration/json/integration-component.zip");
        MockMultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, zipInputStream
        );

        MvcResult result = mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedResult = """
                {
                    "groups":[
                        { "status":"ERROR", "cause":"Campaign not integrated because one or more questionnaire models failed to integrate" }
                    ],
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

    @Test
    @DisplayName("on integrate context with a valid zip, the group is created with its questionnaireIds wired via the join table")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void integrateContext04_nominal_wires_group_to_questionnaire_models() throws Exception {
        // Given
        MockMultipartFile uploadedFile = new MockMultipartFile(
                "file", "nominal.zip", MediaType.APPLICATION_OCTET_STREAM_VALUE, buildNominalZip());

        // When
        mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups[0].id").value("NOMINAL-GRP"))
                .andExpect(jsonPath("$.groups[0].status").value("CREATED"))
                .andExpect(jsonPath("$.questionnaireModels[*].status",
                        Matchers.everyItem(Matchers.is("CREATED"))));

        // And: the group exposes its questionnaireIds, proving the join table was populated
        mockMvc.perform(get("/api/admin/campaigns/NOMINAL-GRP")
                        .with(authentication(authenticatedUserTestHelper.getAdminUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("NOMINAL-GRP"))
                .andExpect(jsonPath("$.questionnaireIds[*]")
                        .value(Matchers.containsInAnyOrder("nominal-qm-1", "nominal-qm-2")));
    }

    private static byte[] buildNominalZip() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            addEntry(zos, "campaign.json", """
                    {"id":"nominal-grp","label":"Nominal label","metadata":{}}""");
            addEntry(zos, "nomenclatures.json", """
                    [{"id":"nominal-nom","label":"Nominal nom","filename":"nominal-nom.json"}]""");
            addEntry(zos, "nomenclatures/nominal-nom.json", """
                    [{"id":"row-1","label":"Row 1"}]""");
            addEntry(zos, "questionnaireModels.json", """
                    [
                        {"id":"nominal-qm-1","label":"Nominal QM 1","filename":"nominal-qm-1.json","required-nomenclatures":["nominal-nom"]},
                        {"id":"nominal-qm-2","label":"Nominal QM 2","filename":"nominal-qm-2.json","required-nomenclatures":["nominal-nom"]}
                    ]""");
            addEntry(zos, "questionnaireModels/nominal-qm-1.json", "{}");
            addEntry(zos, "questionnaireModels/nominal-qm-2.json", "{}");
        }
        return baos.toByteArray();
    }

    private static void addEntry(ZipOutputStream zos, String name, String content) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(content.getBytes());
        zos.closeEntry();
    }
}
