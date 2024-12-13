package fr.insee.queen.application.dataset.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

class DataSetUnCipherTests extends ContainerConfiguration {

    private final DataSetCommonAssertions dataSetCommonAssertions;

    public DataSetUnCipherTests(@Autowired MockMvc mockMvc) {
        this.dataSetCommonAssertions = new DataSetCommonAssertions(mockMvc);
    }

    @Test
    @DisplayName("on creating dataset, create the dataset")
    @Sql(value = ScriptConstants.TRUNCATE_SQL_SCRIPT, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void createDataset01() throws Exception {
        dataSetCommonAssertions.createDataset01();
    }

    @Test
    void createDataset02() throws Exception {
        dataSetCommonAssertions.createDataset02();
    }

    @Test
    void createDataset03() throws Exception {
        dataSetCommonAssertions.createDataset03();
    }
}
