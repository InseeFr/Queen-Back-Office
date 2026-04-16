package fr.insee.queen.application.integration.component.builder.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistreUrlValidatorTest {

    private RegistreUrlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RegistreUrlValidator("http://example.com");
    }

    @Test
    @DisplayName("When url is valid and starts with base url, then returns true")
    void test_valid_url_returns_true() {
        assertThat(validator.isValidUrl("http://example.com/some/path")).isTrue();
    }

    @Test
    @DisplayName("When url does not start with base url, then returns false")
    void test_url_not_starting_with_base_url_returns_false() {
        assertThat(validator.isValidUrl("http://malicious.com/some/path")).isFalse();
    }

    @Test
    @DisplayName("When url is null, then returns false")
    void test_null_url_returns_false() {
        assertThat(validator.isValidUrl(null)).isFalse();
    }

    @Test
    @DisplayName("When url is blank, then returns false")
    void test_blank_url_returns_false() {
        assertThat(validator.isValidUrl("   ")).isFalse();
    }

    @Test
    @DisplayName("When url is empty, then returns false")
    void test_empty_url_returns_false() {
        assertThat(validator.isValidUrl("")).isFalse();
    }

    @Test
    @DisplayName("When url is malformed despite starting with base url, then returns false")
    void test_malformed_url_returns_false() {
        assertThat(validator.isValidUrl("http://example.com/path with spaces")).isFalse();
    }

    @Test
    @DisplayName("When base url is blank, then returns false regardless of url")
    void test_blank_base_url_returns_false() {
        RegistreUrlValidator blankBaseValidator = new RegistreUrlValidator("   ");
        assertThat(blankBaseValidator.isValidUrl("http://example.com/path")).isFalse();
    }

    @Test
    @DisplayName("When base url is null, then returns false regardless of url")
    void test_null_base_url_returns_false() {
        RegistreUrlValidator nullBaseValidator = new RegistreUrlValidator(null);
        assertThat(nullBaseValidator.isValidUrl("http://example.com/path")).isFalse();
    }
}