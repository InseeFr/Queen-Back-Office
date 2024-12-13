package fr.insee.queen.application.surveyunit.integration;

import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.infrastructure.db.configuration.DataFactory;
import fr.insee.queen.infrastructure.db.data.entity.unciphered.UncipheredDataDB;
import fr.insee.queen.infrastructure.db.data.repository.jpa.DataJpaRepository;
import fr.insee.queen.infrastructure.db.data.repository.jpa.UncipheredDataJpaRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

class DataUnCipherTests extends ContainerConfiguration {
    private final DataCommonAssertions dataCommonAssertions;

    public DataUnCipherTests(@Autowired MockMvc mockMvc) {
        this.dataCommonAssertions = new DataCommonAssertions(mockMvc);
    }

    @Autowired
    private DataJpaRepository jpaRepository;

    @Autowired
    private DataFactory dataFactory;


    @Test
    @DisplayName("Check Ciphered Data repository is loaded")
    void checkRepository() {
        assertThat(jpaRepository)
                .isInstanceOf(UncipheredDataJpaRepository.class);
    }

    @Test
    @DisplayName("Check Ciphered Data repository is loaded")
    void checkDataFactory() {
        assertThat(dataFactory.buildData(null, null))
                .isInstanceOf(UncipheredDataDB.class);
    }

    @Test
    void on_get_data_return_data() throws Exception {
        dataCommonAssertions.on_get_data_return_data();
    }

    @Test
    void on_get_data_when_su_not_exist_return_404() throws Exception {
        dataCommonAssertions.on_get_data_when_su_not_exist_return_404();
    }

    @Test
    void on_get_data_when_su_id_invalid_return_400() throws Exception {
        dataCommonAssertions.on_get_data_when_su_id_invalid_return_400();
    }

    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void on_update_data_data_is_updated() throws Exception {
        dataCommonAssertions.on_update_data_data_is_updated();
    }

    @Test
    void on_update_data_when_su_not_exist_return_404() throws Exception {
        dataCommonAssertions.on_update_data_when_su_not_exist_return_404();
    }

    @Test
    void on_update_data_when_su_id_invalid_return_400() throws Exception {
        dataCommonAssertions.on_update_data_when_su_id_invalid_return_400();
    }

    @Test
    void on_update_data_when_data_not_json_object_node_return_400() throws Exception {
        dataCommonAssertions.on_update_data_when_data_not_json_object_node_return_400();
    }

    @Test
    void on_get_data_when_anonymous_user_return_401() throws Exception {
        dataCommonAssertions.on_get_data_when_anonymous_user_return_401();
    }

    @Test
    void on_update_data_when_anonymous_user_return_401() throws Exception {
        dataCommonAssertions.on_update_data_when_anonymous_user_return_401();
    }

    @Test
    @DisplayName("Given survey unit data with collected data, when inserting partial collected data, then merge collected datas")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void updateCollectedData02() throws Exception {
        dataCommonAssertions.updateCollectedData02();
    }

    @Test
    @DisplayName("Given survey unit with no collected json data, when updating data then insert partial data as collected data")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void updateCollectedData01() throws Exception {
        dataCommonAssertions.updateCollectedData01();
    }

    @Test
    @DisplayName("Given invalid survey unit id, when updating collected data then throw bad request")
    void updateCollectedDataError02() throws Exception {
        dataCommonAssertions.updateCollectedDataError02();
    }

    @Test
    @DisplayName("Given invalid json collected input data, when updating collected data then throw bad request")
    void updateCollectedDataError03() throws Exception {
        dataCommonAssertions.updateCollectedDataError03();
    }

    @Test
    @DisplayName("Given an anoymous user, when updating collected data then return unauthenticated error")
    void updateCollectedDataError04() throws Exception {
        dataCommonAssertions.updateCollectedDataError04();
    }
}
