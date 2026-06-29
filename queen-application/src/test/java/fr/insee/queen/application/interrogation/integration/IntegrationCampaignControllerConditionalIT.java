package fr.insee.queen.application.interrogation.integration;

import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "application.group.kind=PARTITION")
class IntegrationCampaignControllerConditionalIT {

    @Autowired
    private MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @Test
    @DisplayName("on integrate context, when kind=PARTITION the endpoint is not registered and returns 404")
    void integrateContext_whenPartition_returns404() throws Exception {
        MockMultipartFile uploadedFile = new MockMultipartFile(
                "file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello".getBytes()
        );

        mockMvc.perform(multipart("/api/campaign/context")
                        .file(uploadedFile)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isNotFound());
    }
}
