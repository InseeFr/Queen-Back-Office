package fr.insee.queen.application.dataset.integration;

import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class DataSetCommonAssertions {
    private final MockMvc mockMvc;
    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    void createDataset01() throws Exception {
        mockMvc.perform(post("/api/create-dataset")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isCreated());
    }

    void createDataset02() throws Exception {
        mockMvc.perform(post("/api/create-dataset")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());
    }

    void createDataset03() throws Exception {
        mockMvc.perform(post("/api/create-dataset")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());
    }
}
