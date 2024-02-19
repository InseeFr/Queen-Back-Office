package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.campaign.infrastructure.dummy.QuestionnaireModelFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;

class QuestionnaireModelExistenceServiceTest {

    private QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private QuestionnaireModelFakeRepository questionnaireModelFakeRepository;

    @BeforeEach
    void init() {
        questionnaireModelFakeRepository = new QuestionnaireModelFakeRepository();
        questionnaireModelExistenceService = new QuestionnaireModelApiExistenceService(questionnaireModelFakeRepository);
    }


    @Test
    @DisplayName("When checking questionnaire existence, if questionnaire does not exist, then throws exception")
    void test_questionnaire_existence_01() {
        questionnaireModelFakeRepository.setQuestionnaireExists(false);
        assertThatThrownBy(() -> questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist("questionnaire-id"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When checking questionnaire existence, if questionnaire exists, resume")
    void test_questionnaire_existence_02() {
        assertThatCode(() -> questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist("questionnaire-id"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When checking questionnaire existence, if questionnaire already exists, then throws exception")
    void test_questionnaire_existence_03() {
        assertThatThrownBy(() -> questionnaireModelExistenceService.throwExceptionIfQuestionnaireAlreadyExist("questionnaire-id"))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    @DisplayName("When checking questionnaire existence, if questionnaire does not exist, resume")
    void test_questionnaire_existence_04() {
        questionnaireModelFakeRepository.setQuestionnaireExists(false);
        assertThatCode(() -> questionnaireModelExistenceService.throwExceptionIfQuestionnaireAlreadyExist("questionnaire-id"))
                .doesNotThrowAnyException();
    }
}
