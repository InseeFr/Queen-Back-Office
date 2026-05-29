package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.infrastructure.dummy.InterrogationFakeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataApiServiceTest {

    private DataApiService dataApiService;
    private InterrogationFakeDao interrogationFakeDao;

    @BeforeEach
    void init() {
        interrogationFakeDao = new InterrogationFakeDao();
        dataApiService = new DataApiService(interrogationFakeDao);
    }

    @Test
    @DisplayName("cleanExtractedDataByIds should delegate to the interrogation repository with the same arguments")
    void cleanExtractedDataByIds_delegates_to_repository() {
        String campaignId = "SIMPSONS2020X00";
        List<String> interrogationIds = List.of("11", "12", "13");

        dataApiService.cleanExtractedDataByIds(campaignId, interrogationIds);

        assertThat(interrogationFakeDao.getCleanedCampaignId()).isEqualTo(campaignId);
        assertThat(interrogationFakeDao.getCleanedInterrogationIds()).containsExactlyElementsOf(interrogationIds);
    }
}
